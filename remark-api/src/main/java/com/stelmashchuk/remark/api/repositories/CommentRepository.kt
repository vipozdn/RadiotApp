package com.stelmashchuk.remark.api.repositories

import com.stelmashchuk.remark.api.HttpConstants
import com.stelmashchuk.remark.api.RemarkService
import com.stelmashchuk.remark.api.Result
import com.stelmashchuk.remark.api.onFailure
import com.stelmashchuk.remark.api.onSuccess
import com.stelmashchuk.remark.api.pojo.CommentWrapper
import com.stelmashchuk.remark.api.pojo.Comments
import com.stelmashchuk.remark.api.pojo.VoteResponse
import com.stelmashchuk.remark.api.pojo.VoteType
import com.stelmashchuk.remark.api.runCatching
import retrofit2.HttpException

class NotAuthUser : Exception()
class CacheNotValid : Exception()
class TooManyRequests : Exception()

class CommentRepository internal constructor(
    private val remarkService: RemarkService,
    private val userStorage: UserStorage,
    private val voteRequestHandler: VoteRequestHandler = VoteRequestHandler(),
    private val commentFinder: CommentFinder = CommentFinder(),
) {

  private var cache: Comments? = null

  fun getReplayByComment(rootId: String): List<CommentWrapper> {
    return commentFinder.getChildComments(cache?.comments.orEmpty(), rootId = rootId)
  }

  suspend fun getComments(
      postUrl: String,
      sort: String = "",
      format: String = "tree",
  ): Result<Comments> {
    if (cache == null) {
      val result = Result.runCatching { remarkService.getCommentsTree(postUrl, sort, format) }
      result.getOrNull()?.also {
        cache = it
      }
      return result
    }

    return Result.success(requireNotNull(cache))
  }

  @Suppress("ReturnCount")
  suspend fun vote(
      commentId: String,
      postUrl: String,
      vote: VoteType,
  ): Result<Comments> {
    if (cache == null) {
      return Result.failure(CacheNotValid())
    }
    if (!userStorage.getCredential().isValid()) {
      return Result.failure(NotAuthUser())
    }
    val voteResponse = Result.runCatching { remarkService.vote(commentId, postUrl, vote.backendCode) }
    return voteRequestHandler.handleVoteResponse(requireNotNull(cache).comments, voteResponse, vote)
  }
}

class VoteRequestHandler {

  fun handleVoteResponse(
      comments: List<CommentWrapper>,
      voteResponse: Result<VoteResponse>,
      vote: VoteType,
  ): Result<Comments> {
    var result = Result.failure<Comments>(Throwable())
    voteResponse.onSuccess {
      val cache = Comments(copyComments(comments, it, vote))
      result = Result.success(cache)
    }
    voteResponse.onFailure { throwable ->
      result = voteErrorHandle(throwable)
    }
    return result
  }

  private fun voteErrorHandle(throwable: Throwable): Result<Comments> {
    if (throwable is HttpException) {
      return when (throwable.code()) {
        HttpConstants.UN_AUTH -> Result.failure(NotAuthUser())
        HttpConstants.TOO_MANY_REQUESTS -> Result.failure(TooManyRequests())
        else -> Result.failure(throwable)
      }
    }

    return Result.failure(throwable)
  }

  private fun copyComments(
      comments: List<CommentWrapper>,
      voteResponse: VoteResponse,
      voteType: VoteType,
  ): List<CommentWrapper> {
    return comments.map {
      if (it.comment.id == voteResponse.id) {
        CommentWrapper(it.comment.copy(score = voteResponse.score, vote = voteType.backendCode), it.replies)
      } else {
        CommentWrapper(it.comment, copyComments(it.replies, voteResponse, voteType))
      }
    }
  }
}
