package com.stelmashchuk.remark.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.stelmashchuk.remark.feature.auth.ui.screen.AuthScreen
import com.stelmashchuk.remark.feature.auth.ui.view.LoginView
import com.stelmashchuk.remark.feature.comments.CommentView

object Destinations {
  const val DATA = "data"
  const val LOGIN = "login"
}

class NavigationActions(navController: NavHostController) {
  val openLogin: () -> Unit = {
    navController.navigate(Destinations.LOGIN)
  }
  val openComments: () -> Unit = {
    navController.navigate(Destinations.DATA)
  }
}


@Composable
fun CommentWidget(postUrl: String) {
  val navController = rememberNavController()
  val navigationActions = remember(navController) {
    NavigationActions(navController)
  }

  val viewModel = viewModel(CommentWidgetViewModel::class.java)
  val loginState by viewModel.loginState.observeAsState()

  when (loginState) {
    LoginState.AUTH -> {
      CommentView(postUrl, navigationActions)
    }
    LoginState.UNAUTH -> {
      NavHost(navController = navController, startDestination = Destinations.DATA) {
        composable(Destinations.DATA) {
          Column {
            LoginView {
              navigationActions.openLogin()
            }
            CommentView(postUrl, navigationActions)
          }
        }
        composable(Destinations.LOGIN) {
          AuthScreen()
        }
      }
    }
  }
}
