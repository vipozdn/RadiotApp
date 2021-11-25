package com.stelmashchuk.remark.api

import com.stelmashchuk.remark.api.comment.CommentStorage
import com.stelmashchuk.remark.api.comment.PostCommentUseCase
import com.stelmashchuk.remark.api.network.HttpConstants
import com.stelmashchuk.remark.api.network.RemarkService
import com.stelmashchuk.remark.api.pojo.VoteResponse
import com.stelmashchuk.remark.api.pojo.VoteType
import com.stelmashchuk.remark.api.repositories.CommentRepository
import com.stelmashchuk.remark.api.repositories.FullComment
import com.stelmashchuk.remark.api.repositories.UserStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

public class CommentDataControllerProvider internal constructor(
    private val remarkService: RemarkService,
    private val siteId: String,
    private val userStorage: UserStorage,
) {

  private val map = HashMap<String, CommentDataController>()

  private val commentMapper: CommentMapper by lazy {
    CommentMapper(userStorage)
  }

  fun getDataController(postUrl: String): CommentDataController {
    return map.getOrPut(postUrl) {
      val commentStorage = CommentStorage()
      CommentDataController(postUrl, siteId, remarkService, CommentRepository(remarkService), commentMapper, commentStorage, PostCommentUseCase(commentStorage, remarkService))
    }
  }
}

sealed class CommentRoot(open val postUrl: String) {
  data class Post(
      override val postUrl: String,
  ) : CommentRoot(postUrl)

  data class Comment(
      override val postUrl: String,
      val commentId: String,
  ) : CommentRoot(postUrl)
}

data class FullCommentInfo(
    val rootComment: FullComment?,
    val comments: List<FullComment>,
)

sealed class RemarkError {
  object NotAuthUser : RemarkError()
  object SomethingWentWrong : RemarkError()
  object TooManyRequests : RemarkError()
}

public class CommentDataController internal constructor(
    private val postUrl: String,
    private val siteId: String,
    private val remarkService: RemarkService,
    private val commentRepository: CommentRepository,
    private val commentMapper: CommentMapper,
    private val commentStorage: CommentStorage,
    private val postCommentUseCase: PostCommentUseCase,
) {

  suspend fun observeComments(commentRoot: CommentRoot): Flow<FullCommentInfo> {
    if (!commentStorage.hasData()) {
      commentStorage.setup(commentMapper.mapCommentsFullComments(commentRepository.getCommentsPlain(postUrl)))
    }

    return commentStorage
        .observableComment(commentRoot)
        .map { comments ->
          val rootComment: FullComment? = when (commentRoot) {
            is CommentRoot.Comment -> {
              commentStorage.waitForComment(commentRoot.commentId)
            }
            is CommentRoot.Post -> null
          }

          FullCommentInfo(rootComment, comments)
        }
  }

  suspend fun vote(
      commentId: String,
      vote: VoteType,
  ): RemarkError? {
    val voteResponse = Result.runCatching { remarkService.vote(commentId, postUrl, vote.backendCode) }
    return handleResponse(voteResponse, commentId, vote)
  }

  suspend fun postComment(
      commentRoot: CommentRoot,
      text: String,
  ): RemarkError? {
    return postCommentUseCase.postComment(commentRoot, text, postUrl, siteId)
  }

  suspend fun delete(commentId: String): Any? {
    val deletedComment = remarkService.delete(commentId)
    commentStorage.remove(deletedComment.id)
    return null
  }

  private suspend fun handleResponse(voteResponse: Result<VoteResponse>, commentId: String, vote: VoteType): RemarkError? {
    return if (voteResponse.isSuccess) {
      handleSuccessVote(commentId, voteResponse, vote)
    } else {
      handleOtherCases(voteResponse)
    }
  }

  private fun handleOtherCases(voteResponse: Result<VoteResponse>): RemarkError? {
    if (voteResponse.isFailure) {
      return when ((voteResponse.exceptionOrNull() as? HttpException)?.code()) {
        HttpConstants.UN_AUTH -> RemarkError.NotAuthUser
        HttpConstants.TOO_MANY_REQUESTS -> RemarkError.TooManyRequests
        else -> null
      }
    }

    return RemarkError.SomethingWentWrong
  }

  private suspend fun handleSuccessVote(commentId: String, voteResponse: Result<VoteResponse>, vote: VoteType): Nothing? {
    val comment = commentStorage.waitForComment(commentId)
        .copy(score = voteResponse.getOrNull()?.score!!, vote = vote.backendCode)

    commentStorage.replace(commentId, comment)
    return null
  }
}
