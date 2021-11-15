package com.stelmashchuk.remark.feature.comments

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.accompanist.coil.rememberCoilPainter
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.CommentRoot
import com.stelmashchuk.remark.di.Graph
import com.stelmashchuk.remark.feature.CommentViewEvent
import com.stelmashchuk.remark.feature.auth.ui.button.LoginButton
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import com.stelmashchuk.remark.feature.comments.mappers.ScoreView
import dev.jeziellago.compose.markdowntext.MarkdownText

data class FullCommentsUiModel(
    val root: CommentUiModel?,
    val comments: List<CommentUiModel>,
)

data class CommentUiModel(
    val author: CommentAuthorUiModel,
    val text: String,
    val score: ScoreUiModel,
    val time: String,
    val commentId: String,
    val replyCount: Int?,
)

data class CommentAuthorUiModel(
    val name: String,
    val avatar: String,
)

data class ScoreUiModel(
    val score: String,
    val color: Int,
    @DrawableRes val upRes: Int,
    @DrawableRes val downRes: Int,
)

@Composable
fun OneLevelCommentView(commentRoot: CommentRoot, openReply: (CommentViewEvent.OpenReply) -> Unit, openLogin: () -> Unit) {
  val viewModel: CommentViewModel = viewModel(key = commentRoot.toString(), factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return CommentViewModel(commentRoot, Graph.api.commentDataControllerProvider, CommentUiMapper()) as T
    }
  })

  val state = viewModel.comments.collectAsState()

  val info = viewModel.info.collectAsState()

  Column {
    LoginButton(openLogin)
    when (val data = state.value) {
      is CommentUiState.Data -> {
        CommentsContent(data.data) { event ->
          when (event) {
            is CommentViewEvent.OpenReply -> openReply(event)
            is CommentViewEvent.Vote -> viewModel.vote(event)
          }
        }
      }
      CommentUiState.Empty -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text(text = stringResource(id = R.string.comment_empty))
        }
      }
      CommentUiState.Loading -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator()
        }
      }
    }
    if (info.value != null) {
      when (info.value) {
        InfoMessages.TooManyRequests -> {
          Text(text = stringResource(id = R.string.too_many_request), modifier = Modifier.fillMaxWidth())
        }
        null -> {}
      }
    }
  }
}

@Composable
fun CommentsContent(fullCommentsUiModel: FullCommentsUiModel, onEvent: (CommentViewEvent) -> Unit) {
  Column {
    fullCommentsUiModel.root?.let {
      OneCommentView(modifier = Modifier, comment = it, onEvent = onEvent)
      Divider()
    }
    LazyColumn(modifier = Modifier
        .padding(8.dp)
        .fillMaxSize()) {
      items(fullCommentsUiModel.comments) { comment ->
        OneCommentView(comment = comment, onEvent = onEvent)
        Divider()
      }
    }
  }
}

@Composable
fun OneCommentView(modifier: Modifier = Modifier, comment: CommentUiModel, onEvent: (CommentViewEvent) -> Unit) {
  @Suppress("MagicNumber")
  Row(modifier = modifier) {
    Image(
        painter = rememberCoilPainter(
            request = comment.author.avatar,
            requestBuilder = fun ImageRequest.Builder.(_: IntSize): ImageRequest.Builder {
              return transformations(CircleCropTransformation())
            },
            shouldRefetchOnSizeChange = { _, _ -> true },
        ),
        contentDescription = "Avatar ${comment.author.name}",
        modifier = Modifier
            .padding(8.dp)
            .size(40.dp),
    )
    Column {
      Row(modifier = Modifier.padding(paddingValues = PaddingValues(vertical = 8.dp))) {
        Text(text = comment.author.name, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = comment.time, fontSize = 14.sp)
      }
      MarkdownText(markdown = comment.text)
      Row {
        ScoreView(score = comment.score) { voteType ->
          onEvent(CommentViewEvent.Vote(comment.commentId, voteType))
        }
        comment.replyCount?.let {
          Button(
              onClick = { onEvent(CommentViewEvent.OpenReply(comment.commentId)) },
          ) {
            Text(text = stringResource(R.string.reply, it))
          }
        }
      }
    }
  }
}
