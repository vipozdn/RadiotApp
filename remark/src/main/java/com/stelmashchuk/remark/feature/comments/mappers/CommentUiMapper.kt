package com.stelmashchuk.remark.feature.comments.mappers

import com.stelmashchuk.remark.api.new.CommentInfo
import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.feature.comments.CommentUiModel

class CommentUiMapper(
    private val scoreUiMapper: ScoreUiMapper = ScoreUiMapper(),
    private val timeMapper: TimeMapper = TimeMapper(),
    private val userUiMapper: UserUiMapper = UserUiMapper(),
) {

  fun mapOneLevel(comments: List<CommentInfo>): List<CommentUiModel> {
    return comments.map {
      mapSingleComment(it.comment, it.replayCount)
    }
  }

  private fun mapSingleComment(comment: Comment, replyCount: Int): CommentUiModel {
    return CommentUiModel(
        author = userUiMapper.map(comment.user),
        text = comment.text,
        score = scoreUiMapper.map(comment),
        time = timeMapper.map(comment.time),
        commentId = comment.id,
        replyCount = replyCount.takeIf { it > 0 },
    )
  }
}
