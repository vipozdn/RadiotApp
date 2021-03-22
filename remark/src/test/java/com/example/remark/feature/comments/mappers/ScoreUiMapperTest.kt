package com.example.remark.feature.comments.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import arrow.core.identity
import com.example.remark.R
import com.example.remark.data.Comment
import com.example.remark.feature.comments.ScoreUiModel
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class ScoreUiMapperTest {

  private val scoreUiMapper = ScoreUiMapper()

  @Test
  fun `Verify create score model`() {
    val comment = mockk<Comment>(relaxed = true) {
      every { id } answers { "id" }
      every { score } answers { 2 }
    }

    scoreUiMapper.map(comment) shouldBe ScoreUiModel(
        "id",
        "2",
        Color.Green.toArgb(),
        R.drawable.up_unselected,
        R.drawable.down_unselect,
    )
  }

}