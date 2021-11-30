package com.stelmashchuk.remark.feature.comments.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.comment.FullComment
import com.stelmashchuk.remark.feature.comments.ScoreUiModel

class ScoreUiMapper {

  fun map(comment: FullComment): ScoreUiModel {
    return ScoreUiModel(
        comment.score.toString(),
        getColor(comment.score),
        getUpIcon(comment),
        getDownIcon(comment),
        comment.id,
    )
  }

  private fun getUpIcon(comment: FullComment): Int {
    return if (comment.vote == 1) {
      R.drawable.up
    } else {
      R.drawable.up_unselected
    }
  }

  private fun getDownIcon(comment: FullComment): Int {
    return if (comment.vote == -1) {
      R.drawable.down
    } else {
      R.drawable.down_unselect
    }
  }

  private fun getColor(score: Long): Int {
    return when {
      score > 0L -> Color.Green
      score < 0L -> Color.Red
      else -> Color.Gray
    }.toArgb()
  }
}
