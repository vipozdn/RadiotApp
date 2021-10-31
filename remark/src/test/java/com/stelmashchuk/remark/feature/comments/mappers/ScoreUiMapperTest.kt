package com.stelmashchuk.remark.feature.comments.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.feature.comments.ScoreUiModel
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class ScoreUiMapperTest {

  private val scoreUiMapper = ScoreUiMapper()

  @Test
  fun `Verify create score model`() {
    val comment = mockk<Comment>(relaxed = true) {
      every { score } answers { 2 }
    }

    scoreUiMapper.map(comment) shouldBe ScoreUiModel(
        "2",
        Color.Green.toArgb(),
        R.drawable.up_unselected,
        R.drawable.down_unselect,
    )
  }

}