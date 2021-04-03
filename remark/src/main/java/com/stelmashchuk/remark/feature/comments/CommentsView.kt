package com.stelmashchuk.remark.feature.comments

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.common.FullSizeProgress
import com.stelmashchuk.remark.data.pojo.VoteType
import com.stelmashchuk.remark.feature.comments.mappers.ScoreView
import dev.jeziellago.compose.markdowntext.MarkdownText

data class CommentUiModel(
    val userName: String,
    val text: String,
    val level: Int,
    val score: ScoreUiModel,
    val time: String,
    val commentId: String,
)

data class ScoreUiModel(
    val score: String,
    val color: Int,
    @DrawableRes val upRes: Int,
    @DrawableRes val downRes: Int,
)

@Composable
fun CommentView(postUrl: String) {
  val viewModel: CommentsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")

      return CommentsViewModel(postUrl = postUrl) as T
    }
  })

  val data by viewModel.commentsLiveData.observeAsState()
  val message by viewModel.messageLiveData.observeAsState()

  data?.let { state ->
    when (state) {
      is ViewState.Data -> {
        Scaffold(
            backgroundColor = Color.White,
            snackbarHost = {
              message?.let {
                Snackbar {
                  Text(text = stringResource(id = R.string.too_many_request))
                }
              }
            }
        ) {
          CommentContent(comments = state.data, viewModel::vote)
        }
      }
      ViewState.Loading -> FullSizeProgress()
    }
  }
}

@Composable
fun CommentContent(comments: List<CommentUiModel>, onVote: (commentId: String, voteType: VoteType) -> Unit) {
  LazyColumn(modifier = Modifier
      .padding(8.dp)
      .fillMaxSize()) {
    items(comments) { comment ->
      @Suppress("MagicNumber")
      Column(modifier = Modifier.padding(start = (8 * comment.level).dp)) {
        Text(text = comment.userName, style = MaterialTheme.typography.subtitle2)
        MarkdownText(markdown = comment.text)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
          Text(text = comment.time)
          ScoreView(score = comment.score) { voteType ->
            onVote(comment.commentId, voteType)
          }
        }
      }
      Divider()
    }
  }
}
