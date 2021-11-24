package com.stelmashchuk.remark.api

import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.api.repositories.FullComment
import com.stelmashchuk.remark.api.repositories.UserStorage

class CommentMapper(private val userStorage: UserStorage) {
  fun mapCommentsFullComments(comments: List<Comment>): List<FullComment> {
    return comments.map { comment ->
      FullComment(
          id = comment.id,
          parentId = comment.parentId,
          text = comment.text,
          score = comment.score,
          user = comment.user,
          time = comment.time,
          vote = comment.vote,
          replyCount = comments.count { it.parentId == comment.id },
          isCurrentUserAuthor = false
      )
    }
  }

  fun mapOneCommentToFullComment(comment: Comment): FullComment {
    return FullComment(
        id = comment.id,
        parentId = comment.parentId,
        text = comment.text,
        score = comment.score,
        user = comment.user,
        time = comment.time,
        vote = comment.vote,
        replyCount = 0,
        isCurrentUserAuthor = true
    )
  }
}

