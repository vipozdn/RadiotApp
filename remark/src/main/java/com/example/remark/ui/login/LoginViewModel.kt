package com.example.remark.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.remark.RemarkSettings
import com.example.remark.data.UserStorage
import com.example.remark.di.Graph

sealed class LoginUiModel {
  object AuthUser : LoginUiModel()
  object UnAuthUser : LoginUiModel()
}

class LoginViewModel(
    userStorage: UserStorage = Graph.userStorage,
) : ViewModel() {

  private val _loginModel = MutableLiveData<LoginUiModel>()
  val loginModel: LiveData<LoginUiModel> = _loginModel

  init {
    userStorage.addListener {
      if (it.isValid()) {
        _loginModel.postValue(LoginUiModel.AuthUser)
      } else {
        _loginModel.postValue(LoginUiModel.UnAuthUser)
      }
    }
  }
}
