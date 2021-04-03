package com.stelmashchuk.remark.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stelmashchuk.remark.feature.auth.ui.screen.AuthScreen
import com.stelmashchuk.remark.feature.comments.CommentView


@Composable
fun CommentWidget(postUrl: String) {
  val viewModel = viewModel(CommentWidgetViewModel::class.java)
  val loginState by viewModel.loginState.observeAsState()

  Column {
    when (loginState) {
      LoginState.UNAUTH -> {
        AuthScreen()
      }
      LoginState.AUTH -> {
        CommentView(postUrl)
      }
    }
  }
}
