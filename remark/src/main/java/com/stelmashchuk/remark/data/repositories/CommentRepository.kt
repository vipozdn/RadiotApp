package com.stelmashchuk.remark.data.repositories

import com.stelmashchuk.remark.RemarkSettings
import com.stelmashchuk.remark.data.RemarkService
import com.stelmashchuk.remark.data.Result
import com.stelmashchuk.remark.data.onFailure
import com.stelmashchuk.remark.data.onSuccess
import com.stelmashchuk.remark.data.pojo.CommentWrapper
import com.stelmashchuk.remark.data.pojo.Comments
import com.stelmashchuk.remark.data.pojo.VoteResponse
import com.stelmashchuk.remark.data.pojo.VoteType
import com.stelmashchuk.remark.data.runCatching
import retrofit2.HttpException

class NotAuthUser : Exception()
class CacheNotValid : Exception()
class TooManyRequests : Exception()

class CommentRepository(
    private val remarkService: RemarkService,
    private val userStorage: UserStorage,
) {

  private lateinit var cache: Comments

  suspend fun getComments(
      postUrl: String,
      sort: String = RemarkSettings.defaultSorting,
      format: String = "tree",
  ): Result<Comments> {
    val result = Result.runCatching { remarkService.getComments(postUrl, sort, format) }
    result.getOrNull()?.also {
      cache = it
    }
    return result
  }

  suspend fun vote(
      commentId: String,
      postUrl: String,
      vote: VoteType,
  ): Result<Comments> {
    if (!this::cache.isInitialized) {
      return Result.failure(CacheNotValid())
    }
    if (!userStorage.getCredential().isValid()) {
      return Result.failure(NotAuthUser())
    }
    val voteResponse = Result.runCatching { remarkService.vote(commentId, postUrl, vote.backendCode) }
    voteResponse.onSuccess {
      cache = Comments(copyComments(cache.comments, it, vote))
      return Result.success(cache)
    }
    voteResponse.onFailure { throwable ->
      return voteErrorHandle(throwable)
    }
    return Result.failure(Exception())
  }

  private fun voteErrorHandle(throwable: Throwable): Result<Comments> {
    if (throwable is HttpException) {
      return when (throwable.code()) {
        401 -> Result.failure(NotAuthUser())
        429 -> Result.failure(TooManyRequests())
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
