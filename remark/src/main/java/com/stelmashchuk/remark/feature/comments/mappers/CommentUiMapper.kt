package com.stelmashchuk.remark.feature.comments.mappers

import com.stelmashchuk.remark.data.pojo.Comment
import com.stelmashchuk.remark.data.pojo.CommentWrapper
import com.stelmashchuk.remark.data.pojo.Comments
import com.stelmashchuk.remark.data.pojo.User
import com.stelmashchuk.remark.feature.comments.CommentUiModel

class CommentUiMapper(
    private val scoreUiMapper: ScoreUiMapper = ScoreUiMapper(),
    private val timeMapper: TimeMapper = TimeMapper(),
    private val userUiMapper: UserUiMapper = UserUiMapper(),
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
        text = comment.text,
        level = level,
        score = scoreUiMapper.map(comment),
        time = timeMapper.map(comment.time),
        commentId = comment.id,
        author = userUiMapper.map(comment.user)
    )
  }
}
