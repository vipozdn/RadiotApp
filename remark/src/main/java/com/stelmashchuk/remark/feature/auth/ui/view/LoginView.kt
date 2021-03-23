package com.stelmashchuk.remark.feature.auth.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginView(openLoginScreen: () -> Unit) {
  val viewModel: LoginViewModel = viewModel(LoginViewModel::class.java)
  val loginModel by viewModel.loginModel.observeAsState()

  loginModel?.let {
    when (it) {
      is LoginUiModel.UnAuthUser -> {
        Button(onClick = { openLoginScreen() }, modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()) {
          Text(text = "Login")
        }
      }
      LoginUiModel.AuthUser -> {

      }
    }
  }
}

