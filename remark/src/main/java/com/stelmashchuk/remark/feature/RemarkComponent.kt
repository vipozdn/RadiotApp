package com.stelmashchuk.remark.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stelmashchuk.remark.api.new.CommentRoot
import com.stelmashchuk.remark.api.pojo.VoteType
import com.stelmashchuk.remark.feature.auth.ui.screen.AuthScreen
import com.stelmashchuk.remark.feature.comments.OneLevelCommentView

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
  const val LOGIN_INTO_REMARK = "LOGIN_INTO_REMARK"
}

private class Actions(navController: NavHostController) {
  val openReply: (CommentViewEvent.OpenReply) -> Unit = {
    navController.navigate("${Destinations.NOT_ROOT_TEMPLATE}/${it.commentId}")
  }

  val openLogin: () -> Unit = {
    navController.navigate(Destinations.LOGIN_INTO_REMARK)
  }
}

@Composable
fun RemarkView(postUrl: String) {
  val navController = rememberNavController()
  val actions = remember(navController) {
    Actions(navController)
  }
  Column {
    NavHost(navController = navController, startDestination = Destinations.ROOT) {
      composable(Destinations.ROOT) {
        OneLevelCommentView(commentRoot = CommentRoot.Post(postUrl), actions.openReply, actions.openLogin)
      }

      composable(Destinations.NOT_ROOT_TEMPLATE + "/{${Destinations.ROOT_COMMENT_URL}}",
          arguments = listOf(
              navArgument(Destinations.ROOT_COMMENT_URL) {
                type = NavType.StringType
              })
      ) {
        OneLevelCommentView(CommentRoot.Comment(postUrl, it.arguments?.getString(Destinations.ROOT_COMMENT_URL)!!), actions.openReply, actions.openLogin)
      }

      composable(Destinations.LOGIN_INTO_REMARK) {
        AuthScreen {
          if (navController.backQueue.last().destination.route == Destinations.LOGIN_INTO_REMARK) {
            navController.popBackStack()
          }
        }
      }
    }
  }
}
