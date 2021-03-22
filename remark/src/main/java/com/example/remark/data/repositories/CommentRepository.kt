package com.example.remark.data.repositories

import com.example.remark.RemarkSettings
import com.example.remark.data.RemarkService
import com.example.remark.data.apiCall
import com.example.remark.data.pojo.CommentWrapper
import com.example.remark.data.pojo.Comments
import com.example.remark.data.pojo.VoteResponse
import com.example.remark.data.pojo.VoteType

class CommentRepository(
    private val remarkService: RemarkService,
) {

  private lateinit var cache: Comments

  suspend fun getComments(
      postUrl: String,
      sort: String = RemarkSettings.defaultSorting,
      format: String = "tree",
  ): Result<Comments> {
    val result = apiCall { remarkService.getComments(postUrl, sort, format) }
    result.getOrNull()?.also {
      cache = it
    }
    return result
  }

  suspend fun vote(
      commentId: String,
      postUrl: String,
      vote: VoteType,
  ): Comments {
    val voteResponse = apiCall { remarkService.vote(commentId, postUrl, vote.backendCode) }
    voteResponse.getOrNull()?.let {
      cache = Comments(copyComments(cache.comments, it, vote))
    }
    return cache
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
