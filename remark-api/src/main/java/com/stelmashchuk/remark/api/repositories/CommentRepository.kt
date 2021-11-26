package com.stelmashchuk.remark.api.repositories

import com.stelmashchuk.remark.api.network.RemarkService
import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.api.pojo.User
import java.time.LocalDateTime

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

class CommentRepository(
    private val remarkService: RemarkService,
) {

  suspend fun getCommentsPlain(
      postUrl: String,
  ): List<Comment> {
    return remarkService.getCommentsPlain(postUrl).comments
  }

}