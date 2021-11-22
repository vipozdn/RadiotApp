package com.stelmashchuk.remark.feature.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stelmashchuk.remark.ResourcesRepository
import com.stelmashchuk.remark.api.CommentRoot
import com.stelmashchuk.remark.di.RemarkComponent
import com.stelmashchuk.remark.feature.auth.ui.screen.AuthScreen
import com.stelmashchuk.remark.feature.comments.OneLevelCommentView

@Composable
fun RemarkView(postUrl: String) {
  val navController = rememberNavController()
  val actions = remember(navController) {
    Actions(navController)
  }

  val viewModel: RemarkViewModel = viewModel(factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return RemarkViewModel(ResourcesRepository(RemarkComponent.context)) as T
    }
  })

  val snackbarHostState = remember { viewModel.snackBar }
  Scaffold(snackbarHost = {
    SnackbarHost(
        hostState = snackbarHostState,
    )
  }) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding)) {
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
}

private object Destinations {
  const val ROOT = "root"
  const val NOT_ROOT_TEMPLATE = "comment%s"
  const val ROOT_COMMENT_URL = "ROOT_COMMENT_URL"
  const val LOGIN_INTO_REMARK = "LOGIN_INTO_REMARK"
}

private class Actions(navController: NavHostController) {
  val openReply: (commentId: String) -> Unit = { commentId ->
    navController.navigate("${Destinations.NOT_ROOT_TEMPLATE}/${commentId}")
  }

  val openLogin: () -> Unit = {
    navController.navigate(Destinations.LOGIN_INTO_REMARK)
  }
}
