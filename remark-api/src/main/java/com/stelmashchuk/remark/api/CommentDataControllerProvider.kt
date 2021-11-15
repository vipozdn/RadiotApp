package com.stelmashchuk.remark.api

import android.util.Log
import com.stelmashchuk.remark.api.network.HttpConstants
import com.stelmashchuk.remark.api.network.RemarkService
import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.api.pojo.VoteResponse
import com.stelmashchuk.remark.api.pojo.VoteType
import com.stelmashchuk.remark.api.repositories.UserStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.reflect.KFunction1

public class CommentDataControllerProvider internal constructor(
    private val remarkService: RemarkService,
    private val userStorage: UserStorage,
) {

  private val map = HashMap<String, CommentDataController>()

  fun getDataController(postUrl: String, scope: CoroutineScope): CommentDataController {
    return map.getOrPut(postUrl) {
      CommentDataController(postUrl, scope, remarkService, userStorage)
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
    val rootComment: CommentInfo?,
    val comments: List<CommentInfo>,
)

data class CommentInfo(
    val comment: Comment,
    val replayCount: Int,
)

sealed class RemarkError {
  object NotAuthUser : RemarkError()
  object SomethingWentWrong : RemarkError()
  object TooManyRequests : RemarkError()
}

public class CommentDataController internal constructor(
    private val postUrl: String,
    private val scope: CoroutineScope,
    private val remarkService: RemarkService,
    private val userStorage: UserStorage,
) {

  private val flow = MutableStateFlow<List<Comment>>(emptyList())

  init {
    scope.launch {
      flow.emit(remarkService.getCommentsPlain(postUrl).comments)
    }
  }

  private fun getReplayCount(commentId: String): Int {
    val comments = flow.value
    return comments.count { it.parentId == commentId }
  }

  fun observeComments(commentRoot: CommentRoot): Flow<FullCommentInfo> {
    fun rootCommentFilter(comment: Comment): Boolean = comment.parentId.isBlank()
    fun notRootCommentFilter(comment: Comment): Boolean = comment.parentId == (commentRoot as CommentRoot.Comment).commentId

    val filterPrediction: KFunction1<Comment, Boolean> = when (commentRoot) {
      is CommentRoot.Comment -> ::notRootCommentFilter
      is CommentRoot.Post -> ::rootCommentFilter
    }

    val rootComment: CommentInfo? = when (commentRoot) {
      is CommentRoot.Comment -> {
        val root: Comment = flow.value.find { it.id == commentRoot.commentId }!!
        CommentInfo(root, getReplayCount(root.id))
      }
      is CommentRoot.Post -> null
    }

    return flow
        .map { comments ->
          FullCommentInfo(rootComment = rootComment, comments = comments.filter(filterPrediction)
              .map {
                CommentInfo(it, getReplayCount(it.id))
              })
        }
  }

  suspend fun vote(
      commentId: String,
      postUrl: String,
      vote: VoteType,
  ): RemarkError? {
    if (!userStorage.getCredential().isValid()) {
      return RemarkError.NotAuthUser
    }
    val voteResponse = Result.runCatching { remarkService.vote(commentId, postUrl, vote.backendCode) }
    return handleResponse(voteResponse, commentId, vote)
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
    val comments = flow.value.toMutableList()
    comments.replaceAll {
      if (it.id == commentId) {
        it.copy(score = voteResponse.getOrNull()?.score!!, vote = vote.backendCode)
      } else {
        it
      }
    }
    flow.emit(comments.toList())
    return null
  }
}
