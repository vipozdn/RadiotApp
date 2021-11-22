package com.stelmashchuk.remark.feature.comments.mappers

import com.stelmashchuk.remark.api.FullCommentInfo
import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.feature.comments.CommentUiModel
import com.stelmashchuk.remark.feature.comments.FullCommentsUiModel

class CommentUiMapper(
    private val scoreUiMapper: ScoreUiMapper = ScoreUiMapper(),
    private val timeMapper: TimeMapper = TimeMapper(),
    private val userUiMapper: UserUiMapper = UserUiMapper(),
) {

  fun mapOneLevel(fullCommentInfo: FullCommentInfo): FullCommentsUiModel {
    return FullCommentsUiModel(
        root = fullCommentInfo.rootComment?.let { mapSingleComment(it.comment) },
        comments = fullCommentInfo.comments.map {
          mapSingleComment(it.comment, it.replayCount)
        }
    )
  }

  private fun mapSingleComment(comment: Comment, replyCount: Int? = null): CommentUiModel {
    return CommentUiModel(
        author = userUiMapper.map(comment.user),
        text = comment.text,
        score = scoreUiMapper.map(comment),
        time = timeMapper.map(comment.time),
        commentId = comment.id,
        replyCount = replyCount?.takeIf { it > 0 },
        isDeleteAvailable = true
    )
  }
}
