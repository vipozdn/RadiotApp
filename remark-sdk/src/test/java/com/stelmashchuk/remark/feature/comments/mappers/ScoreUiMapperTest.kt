package com.stelmashchuk.remark.feature.comments.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.feature.comments.ScoreUiModel
import com.stelmashchuk.tooling.mockFullComment
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test

internal class ScoreUiMapperTest {

  private val scoreUiMapper = ScoreUiMapper()

  @Test
  fun `Verify create score model`() {
    val commentId = CommentId("commentId")
    val comment = mockFullComment(id = commentId, score = 2)

    scoreUiMapper.map(comment) shouldBe ScoreUiModel(
        "2",
        Color.Green.toArgb(),
        R.drawable.up_unselected,
        R.drawable.down_unselect,
        commentId,
    )
  }
}
