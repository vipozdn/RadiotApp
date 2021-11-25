package com.stelmashchuk.remark.api

import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.api.repositories.FullComment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommentMapper {

  private val backendFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

  fun mapCommentsFullComments(comments: List<Comment>): List<FullComment> {
    return comments.map { comment ->
      FullComment(
          id = comment.id,
          parentId = comment.parentId,
          text = comment.text,
          score = comment.score,
          user = comment.user,
          time = LocalDateTime.parse(comment.time, backendFormatter),
          vote = comment.vote,
          replyCount = comments.count { it.parentId == comment.id },
          isCurrentUserAuthor = false
      )
    }
  }
}

