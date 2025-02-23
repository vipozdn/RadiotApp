package com.stelmashchuk.remark.feature.comments.mappers

import com.stelmashchuk.remark.api.comment.FullCommentInfo
import com.stelmashchuk.remark.feature.comments.FullCommentsUiModel

internal class CommentUiMapper(
    private val singleCommentMapper: SingleCommentMapper,
) {

  fun mapOneLevel(fullCommentInfo: FullCommentInfo): FullCommentsUiModel {
    return FullCommentsUiModel(
        root = fullCommentInfo.rootComment?.let { singleCommentMapper.map(it) },
        comments = fullCommentInfo.comments.sortedByDescending { it.time }.map {
          singleCommentMapper.map(it)
        }
    )
  }
}
