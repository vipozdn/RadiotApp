package com.stelmashchuk.remark.feature.comments.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.api.comment.FullComment
import com.stelmashchuk.remark.feature.comments.ScoreUiModel
import io.kotlintest.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class ScoreUiMapperTest {

  private val scoreUiMapper = ScoreUiMapper()

  @Test
  fun `Verify create score model`() {
    val commentId = CommentId("commentId")
    val comment = mockFullComment(commentId, 2)

    scoreUiMapper.map(comment) shouldBe ScoreUiModel(
        "2",
        Color.Green.toArgb(),
        R.drawable.up_unselected,
        R.drawable.down_unselect,
        commentId,
    )
  }

  private fun mockFullComment(commentId: CommentId, score: Long): FullComment {
    return FullComment(
        id = commentId,
        score = score,
        parentId = CommentId(""),
        text = "",
        user = mockk(),
        time = mockk(),
        vote = 0,
        replyCount = 0,
        isCurrentUserAuthor = true,
    )
  }
}
