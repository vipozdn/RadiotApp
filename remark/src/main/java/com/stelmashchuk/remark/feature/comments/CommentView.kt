package com.stelmashchuk.remark.feature.comments

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.accompanist.coil.rememberCoilPainter
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.new.CommentRoot
import com.stelmashchuk.remark.api.pojo.VoteType
import com.stelmashchuk.remark.di.Graph
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import com.stelmashchuk.remark.feature.comments.mappers.ScoreView
import dev.jeziellago.compose.markdowntext.MarkdownText

data class CommentUiModel(
    val author: CommentAuthorUiModel,
    val text: String,
    val score: ScoreUiModel,
    val time: String,
    val commentId: String,
    val replyCount: Int? = null,
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

sealed class CommentViewEvent(open val commentId: String) {
  data class OpenReply(
      override val commentId: String,
  ) : CommentViewEvent(commentId)

  data class Vote(
      override val commentId: String,
      val voteType: VoteType,
  ) : CommentViewEvent(commentId)
}

private object Destinations {
  const val ROOT = "root"
  const val NOT_ROOT_TEMPLATE = "comment%s"
  const val ROOT_COMMENT_URL = "ROOT_COMMENT_URL"
}

private class Actions(navController: NavHostController) {
  val openReply: (CommentViewEvent.OpenReply) -> Unit = {
    navController.navigate("${Destinations.NOT_ROOT_TEMPLATE}/${it.commentId}")
  }
}

@Composable
fun CommentView(postUrl: String) {
  val navController = rememberNavController()
  val actions = remember(navController) {
    Actions(navController)
  }
  NavHost(navController = navController, startDestination = Destinations.ROOT) {
    composable(Destinations.ROOT) {
      OneLevelCommentView(commentRoot = CommentRoot.Post(postUrl), actions.openReply)
    }
    composable(Destinations.NOT_ROOT_TEMPLATE + "/{${Destinations.ROOT_COMMENT_URL}}",
        arguments = listOf(
            navArgument(Destinations.ROOT_COMMENT_URL) {
              type = NavType.StringType
            })
    ) {
      OneLevelCommentView(CommentRoot.Comment(postUrl, it.arguments?.getString(Destinations.ROOT_COMMENT_URL)!!), actions.openReply)
    }
  }
}

@Composable
fun OneLevelCommentView(commentRoot: CommentRoot, openReply: (CommentViewEvent.OpenReply) -> Unit) {
  val viewModel: CommentViewModel = viewModel(key = commentRoot.toString(), factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return CommentViewModel(commentRoot, Graph.api.commentDataControllerProvider, CommentUiMapper()) as T
    }
  })

  val state = viewModel.comments
      .collectAsState(initial = emptyList())

  CommentsContent(state.value) { event ->
    when (event) {
      is CommentViewEvent.OpenReply -> openReply(event)
      is CommentViewEvent.Vote -> TODO()
    }
  }
}

@Composable
fun CommentsContent(comments: List<CommentUiModel>, onEvent: (CommentViewEvent) -> Unit) {
  LazyColumn(modifier = Modifier
      .padding(8.dp)
      .fillMaxSize()) {
    items(comments) { comment ->
      CommentView(comment, onEvent)
      Divider()
    }
  }
}

@Composable
fun CommentView(comment: CommentUiModel, onEvent: (CommentViewEvent) -> Unit) {
  @Suppress("MagicNumber")
  Row {
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
              modifier = Modifier.background(Color.Transparent),
          ) {
            Text(text = stringResource(R.string.reply, it), color = Color.Blue)
          }
        }
      }
    }
  }
}
