package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.api.FullComment
import com.stelmashchuk.remark.api.pojo.Comment

class CommentMapper(private val commentTimeMapper: CommentTimeMapper) {

  fun mapCommentsFullComments(comments: List<Comment>): List<FullComment> {
    return comments.map { comment ->
      FullComment(
          id = comment.id,
          parentId = comment.parentId,
          text = comment.text,
          score = comment.score,
          user = comment.user,
          time = commentTimeMapper.map(comment.time),
          vote = comment.vote,
          replyCount = comments.count { it.parentId == comment.id },
          isCurrentUserAuthor = false
      )
    }
  }

  fun map(comment: Comment): FullComment {
    return FullComment(
        id = comment.id,
        parentId = comment.parentId,
        text = comment.text,
        score = comment.score,
        user = comment.user,
        time = commentTimeMapper.map(comment.time),
        vote = comment.vote,
        replyCount = 0,
        isCurrentUserAuthor = true
    )
  }
}

