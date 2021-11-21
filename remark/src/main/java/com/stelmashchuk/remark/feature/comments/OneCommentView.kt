package com.stelmashchuk.remark.feature.comments

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.stelmashchuk.remark.di.RemarkComponent
import com.stelmashchuk.remark.feature.auth.ui.button.LoginButton
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import com.stelmashchuk.remark.feature.post.WriteCommentView
import com.stelmashchuk.remark.feature.vote.FullScoreView
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
    val commentId: String,
)

@Composable
fun OneLevelCommentView(commentRoot: CommentRoot, openReply: (commentId: String) -> Unit, openLogin: () -> Unit) {
  val viewModel: CommentViewModel = viewModel(key = commentRoot.toString(), factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return CommentViewModel(commentRoot, CommentUiMapper(), RemarkComponent.api.commentDataControllerProvider.getDataController(commentRoot.postUrl)) as T
    }
  })

  val state = viewModel.comments.collectAsState()

  Column {
    LoginButton(openLogin)
    when (val data = state.value) {
      is CommentUiState.Data -> {
        CommentsContent(data.data, commentRoot, openReply)
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
  }
}

@Composable
fun CommentsContent(fullCommentsUiModel: FullCommentsUiModel, commentRoot: CommentRoot, openReply: (commentId: String) -> Unit) {
  Column {
    fullCommentsUiModel.root?.let {
      OneCommentView(modifier = Modifier, comment = it, postUrl = commentRoot.postUrl, openReply = openReply)
      Divider()
    }
    WriteCommentView(commentRoot)
    LazyColumn(modifier = Modifier
        .padding(8.dp)
        .fillMaxSize()) {
      items(fullCommentsUiModel.comments) { comment ->
        OneCommentView(comment = comment, postUrl = commentRoot.postUrl, openReply = openReply)
        Divider()
      }
    }
  }
}

@Composable
fun OneCommentView(modifier: Modifier = Modifier, comment: CommentUiModel, postUrl: String, openReply: (commentId: String) -> Unit) {
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
        FullScoreView(comment.score, postUrl)
        comment.replyCount?.let {
          Button(
              onClick = { openReply(comment.commentId) },
          ) {
            Text(text = stringResource(R.string.reply, it))
          }
        }
      }
    }
  }
}
