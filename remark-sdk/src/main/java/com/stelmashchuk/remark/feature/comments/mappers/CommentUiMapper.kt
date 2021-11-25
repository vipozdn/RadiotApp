package com.stelmashchuk.remark.feature.comments.mappers

import com.stelmashchuk.remark.api.FullCommentInfo
import com.stelmashchuk.remark.feature.comments.FullCommentsUiModel

class CommentUiMapper(
    private val singleCommentMapper: SingleCommentMapper,
) {

  fun mapOneLevel(fullCommentInfo: FullCommentInfo): FullCommentsUiModel {
    return FullCommentsUiModel(
        root = fullCommentInfo.rootComment?.let { singleCommentMapper.map(it) },
        comments = fullCommentInfo.comments.map {
          singleCommentMapper.map(it)
        }
    )
  }
}
