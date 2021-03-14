package com.example.remark.ui

import com.example.remark.data.Comment
import com.example.remark.data.CommentWrapper
import com.example.remark.data.Comments

class CommentUiMapper {

  fun map(comments: Comments): List<CommentUiModel> {
    return comments.comments.flatMap {
      map(it)
    }
  }

  private fun map(commentWrapper: CommentWrapper, level: Long = 0): List<CommentUiModel> {
    return listOf(mapSingleComment(commentWrapper.comment, level))
        .plus(commentWrapper.replies.map { map(it, level = level + 1) }.flatten())
  }

  private fun mapSingleComment(comment: Comment, level: Long): CommentUiModel {
    return CommentUiModel(
        comment.user.name,
        comment.text,
        level
    )
  }
}