package com.example.remark.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.example.remark.ui.auth.AuthScreen
import com.example.remark.ui.comments.CommentView
import com.example.remark.ui.login.LoginView

object Destinations {
  const val DATA = "data"
  const val LOGIN = "login"
}

class Actions(navController: NavHostController) {
  val openLogin: () -> Unit = {
    navController.navigate(Destinations.LOGIN)
  }
}


@Composable
fun CommentWidget(postUrl: String) {
  val navController = rememberNavController()
  val actions = remember(navController) {
    Actions(navController)
  }
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