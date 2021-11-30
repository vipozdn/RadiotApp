package com.stelmashchuk.remark.api

import com.stelmashchuk.remark.api.comment.CommentMapper
import com.stelmashchuk.remark.api.comment.CommentService
import com.stelmashchuk.remark.api.comment.CommentStorage
import com.stelmashchuk.remark.api.comment.CommentTimeMapper
import com.stelmashchuk.remark.api.comment.HttpConstants
import com.stelmashchuk.remark.api.comment.PostCommentUseCase
import com.stelmashchuk.remark.api.config.VoteResponse
import com.stelmashchuk.remark.api.config.VoteType
import com.stelmashchuk.remark.api.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.time.LocalDateTime

public class CommentDataControllerProvider internal constructor(
    private val commentService: CommentService,
    private val siteId: String,
    private val timeMapper: CommentTimeMapper,
) {

  private val map = HashMap<String, CommentDataController>()

  private val commentMapper: CommentMapper by lazy {
    CommentMapper(timeMapper)
  }

  fun getDataController(postUrl: String): CommentDataController {
    return map.getOrPut(postUrl) {
      val commentStorage = CommentStorage()
      CommentDataController(postUrl, siteId, commentService, commentMapper, commentStorage, PostCommentUseCase(commentStorage, commentService, commentMapper))
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

data class FullComment(
    val id: String,
    val parentId: String,
    val text: String = "",
    val score: Long,
    val user: User,
    val time: LocalDateTime,
    val vote: Int,
    val replyCount: Int,
    val isCurrentUserAuthor: Boolean,
)

public class CommentDataController internal constructor(
    private val postUrl: String,
    private val siteId: String,
    private val commentService: CommentService,
    private val commentMapper: CommentMapper,
    private val commentStorage: CommentStorage,
    private val postCommentUseCase: PostCommentUseCase,
) {

  suspend fun observeComments(commentRoot: CommentRoot): Flow<FullCommentInfo> {
    if (!commentStorage.hasData()) {
      commentStorage.setup(commentMapper.mapCommentsFullComments(commentService.getCommentsPlain(postUrl).comments))
    }

    return commentStorage
        .observableComment(commentRoot)
        .map { comments ->
          val rootComment: FullComment? = when (commentRoot) {
            is CommentRoot.Comment -> {
              commentStorage.waitForComment(commentRoot.commentId)
            }
            else -> null
          }

          FullCommentInfo(rootComment, comments)
        }
  }

  suspend fun observeComment(commentId: String): Flow<FullComment> {
    return commentStorage.observableComment(commentId)
  }

  suspend fun vote(
      commentId: String,
      vote: VoteType,
  ): RemarkError? {
    val voteResponse = Result.runCatching { commentService.vote(commentId, postUrl, vote.backendCode) }
    return handleResponse(voteResponse, commentId, vote)
  }

  suspend fun postComment(
      commentRoot: CommentRoot,
      text: String,
  ): RemarkError? {
    return postCommentUseCase.postComment(commentRoot, text, postUrl, siteId)
  }

  suspend fun delete(commentId: String): Any? {
    val deletedComment = commentService.delete(commentId)
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
