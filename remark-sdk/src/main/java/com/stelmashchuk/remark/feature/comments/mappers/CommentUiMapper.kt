package com.stelmashchuk.remark.feature.comments.mappers

import com.stelmashchuk.remark.api.FullCommentInfo
import com.stelmashchuk.remark.api.repositories.FullComment
import com.stelmashchuk.remark.feature.comments.CommentUiModel
import com.stelmashchuk.remark.feature.comments.FullCommentsUiModel

class CommentUiMapper(
    private val scoreUiMapper: ScoreUiMapper = ScoreUiMapper(),
    private val timeMapper: TimeMapper = TimeMapper(),
    private val userUiMapper: UserUiMapper = UserUiMapper(),
) {

  fun mapOneLevel(fullCommentInfo: FullCommentInfo): FullCommentsUiModel {
    return FullCommentsUiModel(
        root = fullCommentInfo.rootComment?.let { mapSingleComment(it) },
        comments = fullCommentInfo.comments.map {
          mapSingleComment(it)
        }
    )
  }

  private fun mapSingleComment(comment: FullComment): CommentUiModel {
    return CommentUiModel(
        author = userUiMapper.map(comment.user),
        text = comment.text,
        score = scoreUiMapper.map(comment),
        time = timeMapper.map(comment.time),
        commentId = comment.id,
        replyCount = comment.replyCount,
        isDeleteAvailable = comment.isCurrentUserAuthor,
    )
  }
}
