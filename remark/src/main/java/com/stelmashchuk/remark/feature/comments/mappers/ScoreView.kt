package com.stelmashchuk.remark.feature.comments.mappers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.stelmashchuk.remark.api.pojo.VoteType
import com.stelmashchuk.remark.feature.comments.ScoreUiModel

@Composable
fun ScoreView(score: ScoreUiModel, onVote: (VoteType) -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    VoteButton(onClick = { onVote(VoteType.UP) }) {
      Image(painterResource(score.upRes), "up")
    }
    Text(text = score.score, color = Color(score.color))
    VoteButton(onClick = { onVote(VoteType.DOWN) }) {
      Image(painterResource(score.downRes), "down")
    }
  }
}

@Composable
fun VoteButton(
    onClick: () -> Unit,
    content: @Composable (RowScope.() -> Unit),
) {
  val buttonColors = ButtonDefaults.buttonColors(
      backgroundColor = Color.Transparent,
      contentColor = Color.Transparent
  )

  Button(
      colors = buttonColors,
      onClick = onClick,
      elevation = null,
  ) {
    content()
  }
}
