package com.stelmashchuk.remark.feature.comments.mappers

import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.api.pojo.CommentWrapper
import com.stelmashchuk.remark.api.pojo.Comments
import com.stelmashchuk.remark.feature.comments.CommentUiModel

class CommentUiMapper(
    private val scoreUiMapper: ScoreUiMapper = ScoreUiMapper(),
    private val timeMapper: TimeMapper = TimeMapper(),
    private val userUiMapper: UserUiMapper = UserUiMapper(),
) {

  fun mapOneLevel(comments: List<com.stelmashchuk.remark.api.pojo.CommentWrapper>): List<CommentUiModel> {
    return comments.map {
      mapSingleComment(it.comment, 0, it.replies.size)
    }
  }

  fun map(comments: com.stelmashchuk.remark.api.pojo.Comments): List<CommentUiModel> {
    return comments.comments.flatMap {
      map(it)
    }
  }

  private fun map(commentWrapper: com.stelmashchuk.remark.api.pojo.CommentWrapper, level: Int = 0): List<CommentUiModel> {
    return listOf(mapSingleComment(commentWrapper.comment, level))
        .plus(commentWrapper.replies.map { map(it, level = level + 1) }.flatten())
  }

  private fun mapSingleComment(comment: com.stelmashchuk.remark.api.pojo.Comment, level: Int, replyCount: Int? = null): CommentUiModel {
    return CommentUiModel(
        text = comment.text,
        level = level,
        score = scoreUiMapper.map(comment),
        time = timeMapper.map(comment.time),
        commentId = comment.id,
        author = userUiMapper.map(comment.user),
        replyCount = replyCount
    )
  }
}
