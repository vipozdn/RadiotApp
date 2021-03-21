package com.example.remark.ui.comments

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

data class CommentUiModel(
    val userName: String,
    val text: String,
    val level: Int,
    val score: ScoreUiModel,
    val time: String,
)

data class ScoreUiModel(
    val score: String,
    val color: Int,
    @DrawableRes val upRes: Int,
    @DrawableRes val downRes: Int,
)

@Composable
fun CommentView(postUrl: String) {
  val viewModel: CommentsViewModel = viewModel(CommentsViewModel::class.java)
  viewModel.loadComments(postUrl)

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
            ScoreView(score = comment.score)
          }
        }
        Divider()
      }
    }
  }
}

@Composable
fun ScoreView(score: ScoreUiModel) {
  Row {
    Image(painterResource(score.upRes), "up")
    Text(text = score.score, color = Color(score.color))
    Image(painterResource(score.downRes), "down")
  }
}
