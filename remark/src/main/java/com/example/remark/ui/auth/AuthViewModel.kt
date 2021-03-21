package com.example.remark.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remark.RemarkSettings
import com.example.remark.data.RemarkService
import com.example.remark.di.Graph
import kotlinx.coroutines.launch

data class LoginUiItem(
    val name: String,
    val url: String,
)

class AuthViewModel(
    private val remarkService: RemarkService = Graph.remarkService,
    private val loginItemUiMapper: LoginItemUiMapper = LoginItemUiMapper(),
) : ViewModel() {

  private val _loginUiItem = MutableLiveData<List<LoginUiItem>>()
  val loginUiItem: LiveData<List<LoginUiItem>> = _loginUiItem


  private val _currentLoginProvider = MutableLiveData<String>()
  val currentLoginProvider: LiveData<String> = _currentLoginProvider

  init {
    viewModelScope.launch {
      val config = remarkService.getConfig(RemarkSettings.siteId)
      val loginUiItems = loginItemUiMapper.map(config.authProviders)
      _currentLoginProvider.postValue(loginUiItems.first().url)
      _loginUiItem.postValue(loginUiItems)
    }
  }

  fun selectLoginItem(provider: LoginUiItem) {
    _currentLoginProvider.postValue(provider.url)
  }
}
