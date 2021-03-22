package com.example.remark.feature.auth.ui.screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remark.data.RemarkService
import com.example.remark.data.repositories.UserStorage
import com.example.remark.di.Graph
import kotlinx.coroutines.launch

data class LoginUiItem(
    val name: String,
    val url: String,
)

class AuthViewModel(
    private val remarkService: RemarkService = Graph.remarkService,
    private val loginItemUiMapper: AuthProvidersUiMapper = AuthProvidersUiMapper(),
    private val userStorage: UserStorage = Graph.userStorage,
) : ViewModel() {

  private val _loginUiItem = MutableLiveData<List<LoginUiItem>>()
  val loginUiItem: LiveData<List<LoginUiItem>> = _loginUiItem


  private val _currentLoginProvider = MutableLiveData<String>()
  val currentLoginProvider: LiveData<String> = _currentLoginProvider

  init {
    viewModelScope.launch {
      val config = remarkService.getConfig()
      val loginUiItems = loginItemUiMapper.map(config.authProviders)
      _currentLoginProvider.postValue(loginUiItems.first().url)
      _loginUiItem.postValue(loginUiItems)
    }
  }

  fun selectLoginItem(provider: LoginUiItem) {
    _currentLoginProvider.postValue(provider.url)
  }

  fun cookiesChange(cookies: String) {
    userStorage.saveByCookies(cookies)
  }
}
