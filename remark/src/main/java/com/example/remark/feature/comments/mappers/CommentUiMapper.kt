package com.example.remark.feature.comments.mappers

import com.example.remark.data.Comment
import com.example.remark.data.CommentWrapper
import com.example.remark.data.Comments
import com.example.remark.feature.comments.CommentUiModel

class CommentUiMapper(
    private val scoreUiMapper: ScoreUiMapper = ScoreUiMapper(),
    private val timeMapper: TimeMapper = TimeMapper()
) {

  fun map(comments: Comments): List<CommentUiModel> {
    return comments.comments.flatMap {
      map(it)
    }
  }

  private fun map(commentWrapper: CommentWrapper, level: Int = 0): List<CommentUiModel> {
    return listOf(mapSingleComment(commentWrapper.comment, level))
        .plus(commentWrapper.replies.map { map(it, level = level + 1) }.flatten())
  }

  private fun mapSingleComment(comment: Comment, level: Int): CommentUiModel {
    return CommentUiModel(
        comment.user.name,
        comment.text,
        level,
        scoreUiMapper.map(comment),
        timeMapper.map(comment.time)
    )
  }
}