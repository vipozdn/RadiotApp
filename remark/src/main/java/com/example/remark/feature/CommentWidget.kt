package com.example.remark.feature.comments

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
import com.example.remark.feature.CommentWidgetViewModel
import com.example.remark.feature.LoginState
import com.example.remark.feature.auth.ui.screen.AuthScreen
import com.example.remark.feature.auth.ui.view.LoginView
import kotlin.math.log

object Destinations {
  const val DATA = "data"
  const val LOGIN = "login"
}

class Actions(navController: NavHostController) {
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
  val actions = remember(navController) {
    Actions(navController)
  }

  val viewModel = viewModel(CommentWidgetViewModel::class.java)
  val loginState by viewModel.loginState.observeAsState()

  when (loginState) {
    LoginState.AUTH -> {
      CommentView(postUrl)
    }
    LoginState.UNAUTH -> {
      NavHost(navController = navController, startDestination = Destinations.DATA) {
        composable(Destinations.DATA) {
          Column {
            LoginView {
              actions.openLogin()
            }
            CommentView(postUrl)
          }
        }
        composable(Destinations.LOGIN) {
          AuthScreen()
        }
      }
    }
  }
}