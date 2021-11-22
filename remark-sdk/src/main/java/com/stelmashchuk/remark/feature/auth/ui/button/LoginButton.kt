package com.stelmashchuk.remark.feature.auth.ui.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.RemarkApi
import com.stelmashchuk.remark.di.RemarkComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun LoginButton(openLogin: () -> Unit) {
  val viewModel: LoginButtonViewModel = viewModel(factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return LoginButtonViewModel(remarkApi = RemarkComponent.api) as T
    }
  })

  if (viewModel.needLoginButton.collectAsState().value) {
    Button(onClick = { openLogin() }, modifier = Modifier.fillMaxWidth()) {
      Text(text = stringResource(id = R.string.login))
    }
  }
}

class LoginButtonViewModel(
    remarkApi: RemarkApi,
) : ViewModel() {

  private val _needLoginButton = MutableStateFlow(false)
  val needLoginButton: StateFlow<Boolean> = _needLoginButton

  init {
    remarkApi.addLoginStateListener {
      this.viewModelScope.launch {
        _needLoginButton.emit(!it.isValid())
      }
    }
  }
}
