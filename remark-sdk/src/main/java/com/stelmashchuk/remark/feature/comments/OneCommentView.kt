package com.stelmashchuk.remark.feature.comments

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.api.comment.CommentRoot
import com.stelmashchuk.remark.di.RemarkComponent
import com.stelmashchuk.remark.feature.auth.ui.button.LoginButton
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import com.stelmashchuk.remark.feature.comments.mappers.ScoreUiMapper
import com.stelmashchuk.remark.feature.comments.mappers.SingleCommentMapper
import com.stelmashchuk.remark.feature.comments.mappers.TimeMapper
import com.stelmashchuk.remark.feature.comments.mappers.UserUiMapper
import com.stelmashchuk.remark.feature.delete.ModifyCommentBlock
import com.stelmashchuk.remark.feature.post.WriteCommentView
import com.stelmashchuk.remark.feature.vote.ScoreView
import dev.jeziellago.compose.markdowntext.MarkdownText

internal data class FullCommentsUiModel(
    val root: CommentUiModel?,
    val comments: List<CommentUiModel>,
)

internal data class CommentUiModel(
    val author: CommentAuthorUiModel,
    val text: String,
    val score: ScoreUiModel,
    val time: String,
    val commentId: CommentId,
    val replyCount: Int?,
)

internal data class CommentAuthorUiModel(
    val name: String,
    val avatar: String,
)

internal data class ScoreUiModel(
    val score: String,
    val color: Int,
    @DrawableRes val upRes: Int,
    @DrawableRes val downRes: Int,
    val commentId: CommentId,
)

@Composable
internal fun OneLevelCommentView(commentRoot: CommentRoot, openCommentDetails: (commentId: CommentId) -> Unit, openLogin: () -> Unit) {
  val viewModel: CommentViewModel = viewModel(key = commentRoot.toString(), factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return CommentViewModel(
          commentRoot,
          CommentUiMapper(
              SingleCommentMapper(
                  ScoreUiMapper(),
                  TimeMapper(),
                  UserUiMapper(),
              ),
          ),
          RemarkComponent.api.remarkApiFactory.getDataController(commentRoot.postUrl),
      ) as T
    }
  })

  val state by viewModel.comments.collectAsState()

  Column {
    LoginButton(openLogin)
    when (state) {
      is CommentUiState.Data -> {
        CommentsContent((state as CommentUiState.Data).data, commentRoot, openCommentDetails)
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
internal fun CommentsContent(fullCommentsUiModel: FullCommentsUiModel, commentRoot: CommentRoot, openCommentDetails: (commentId: CommentId) -> Unit) {
  Column {
    fullCommentsUiModel.root?.let {
      OneCommentViewWithImage(modifier = Modifier, comment = it, postUrl = commentRoot.postUrl, openCommentDetails = openCommentDetails)
      Divider()
    }
    WriteCommentView(commentRoot)
    LazyColumn(modifier = Modifier
        .padding(8.dp)
        .fillMaxSize()) {
      items(fullCommentsUiModel.comments) { comment ->
        OneCommentViewWithImage(comment = comment, postUrl = commentRoot.postUrl, openCommentDetails = openCommentDetails)
        Divider()
      }
    }
  }
}

@Composable
internal fun OneCommentViewWithImage(modifier: Modifier = Modifier, comment: CommentUiModel, postUrl: String, openCommentDetails: (commentId: CommentId) -> Unit) {
  @Suppress("MagicNumber")
  Row(
      modifier = modifier
          .clickable { openCommentDetails(comment.commentId) }
          .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
  ) {
    ImageBlock(comment = comment)
    CenterBlock(comment = comment, postUrl = postUrl)
  }
}

@Composable
private fun ImageBlock(comment: CommentUiModel, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
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
            .padding(4.dp)
            .size(40.dp),
    )
    comment.replyCount?.let {
      Text(text = stringResource(R.string.reply, it), fontSize = 18.sp)
    }
  }
}

@Composable
private fun CenterBlock(modifier: Modifier = Modifier, comment: CommentUiModel, postUrl: String) {
  Column(modifier = modifier) {
    Row(modifier = Modifier.padding(paddingValues = PaddingValues(vertical = 8.dp))) {
      Text(text = comment.author.name, fontSize = 14.sp)
      Spacer(modifier = Modifier.width(2.dp))
      Text(text = comment.time, fontSize = 14.sp)
      Spacer(modifier = Modifier.width(2.dp))
    }
    MarkdownText(markdown = comment.text)
    Column {
      ScoreView(score = comment.score, postUrl = postUrl)
      ModifyCommentBlock(comment = comment, postUrl = postUrl)
    }
  }
}
