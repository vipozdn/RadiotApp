package com.stelmashchuk.remark.feature.vote

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stelmashchuk.remark.api.comment.VoteType
import com.stelmashchuk.remark.di.RemarkComponent
import com.stelmashchuk.remark.feature.comments.ScoreUiModel

@Composable
internal fun FullScoreView(modifier: Modifier, score: ScoreUiModel, postUrl: String) {
  val viewModel: ScoreViewModel = viewModel(key = score.commentId.raw, factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return ScoreViewModel(score.commentId, RemarkComponent.api.remarkApiFactory.getDataController(postUrl)) as T
    }
  })

  ScoreView(score = score, modifier = modifier) { voteType ->
    viewModel.vote(voteType)
  }
}

@Composable
internal fun ScoreView(modifier: Modifier, score: ScoreUiModel, onVote: (VoteType) -> Unit = {}) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
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
internal fun VoteButton(
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
