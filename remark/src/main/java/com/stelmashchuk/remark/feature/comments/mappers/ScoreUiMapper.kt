package com.stelmashchuk.remark.feature.comments.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.feature.comments.ScoreUiModel

class ScoreUiMapper {

  fun map(comment: Comment): ScoreUiModel {
    return ScoreUiModel(
        comment.score.toString(),
        getColor(comment.score),
        getUpIcon(comment),
        getDownIcon(comment),
    )
  }

  private fun getUpIcon(comment: Comment): Int {
    return if (comment.vote == 1) {
      R.drawable.up
    } else {
      R.drawable.up_unselected
    }
  }

  private fun getDownIcon(comment: Comment): Int {
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
