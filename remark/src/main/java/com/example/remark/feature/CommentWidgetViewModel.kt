package com.example.remark.feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.remark.data.repositories.UserStorage
import com.example.remark.di.Graph

enum class LoginState {
  AUTH, UNAUTH
}

class CommentWidgetViewModel(
    private val userStorage: UserStorage = Graph.userStorage,
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

}