package com.stelmashchuk.remark.feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stelmashchuk.remark.api.repositories.UserStorage
import com.stelmashchuk.remark.di.Graph

enum class LoginState {
  AUTH, UNAUTH
}

class CommentWidgetViewModel(
    private val userStorage: com.stelmashchuk.remark.api.repositories.UserStorage = Graph.userStorage,
) : ViewModel() {

  private val _loginState = MutableLiveData<LoginState>()
  val loginState: LiveData<LoginState> = _loginState

  init {
    userStorage.addListener {
      _loginState.postValue(if (it.isValid()) {
        LoginState.AUTH
      } else {
        LoginState.UNAUTH
      })
    }
  }

  fun logout() {
    userStorage.logout()
  }
}
