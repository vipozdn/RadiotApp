package com.example.remark.feature.comments

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remark.data.VoteType

data class CommentUiModel(
    val userName: String,
    val text: String,
    val level: Int,
    val score: ScoreUiModel,
    val time: String,
)

data class ScoreUiModel(
    val commentId: String,
    val score: String,
    val color: Int,
    @DrawableRes val upRes: Int,
    @DrawableRes val downRes: Int,
)

@Composable
fun CommentView(postUrl: String) {
  val viewModel: CommentsViewModel = viewModel(CommentsViewModel::class.java)
  viewModel.start(postUrl)

  val data by viewModel.comments.observeAsState()

  data?.let {
    LazyColumn(modifier = Modifier.padding(8.dp)) {
      items(it) { comment ->
        Column(modifier = Modifier.padding(start = (8 * comment.level).dp)) {
          Text(text = comment.userName, style = MaterialTheme.typography.subtitle2)
          Text(text = comment.text, style = MaterialTheme.typography.body1)
          Row(
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = comment.time)
            ScoreView(score = comment.score, viewModel)
          }
        }
        Divider()
      }
    }
  }
}

@Composable
fun ScoreView(score: ScoreUiModel, viewModel: CommentsViewModel) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    VoteButton(onClick = { viewModel.vote(score.commentId, VoteType.UP) }) {
      Image(painterResource(score.upRes), "up")
    }
    Text(text = score.score, color = Color(score.color))
    VoteButton(onClick = { viewModel.vote(score.commentId, VoteType.DOWN) }) {
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
