package com.stelmashchuk.remark.feature.comments.mappers

import com.stelmashchuk.remark.api.comment.FullComment
import com.stelmashchuk.remark.feature.comments.CommentUiModel

internal class SingleCommentMapper(
    private val scoreUiMapper: ScoreUiMapper,
    private val timeMapper: TimeMapper,
    private val userUiMapper: UserUiMapper,
) {

  fun map(comment: FullComment): CommentUiModel {
    return CommentUiModel(
        author = userUiMapper.map(comment.user),
        text = comment.text,
        score = scoreUiMapper.map(comment),
        time = timeMapper.map(comment.time),
        commentId = comment.id,
        replyCount = comment.replyCount.takeIf { it != 0 },
    )
  }

}