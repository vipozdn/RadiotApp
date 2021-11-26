package com.stelmashchuk.remark.feature.auth.ui.screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.RemarkApi
import kotlinx.coroutines.launch

data class LoginUiItem(
    val name: String,
    val url: String,
)

class AuthViewModel(
    private val remarkApi: RemarkApi,
    private val loginItemUiMapper: AuthProvidersUiMapper,
) : ViewModel() {

  private val _loginUiItem = MutableLiveData<List<LoginUiItem>>()
  val loginUiItem: LiveData<List<LoginUiItem>> = _loginUiItem

  private val _currentLoginProvider = MutableLiveData<String>()
  val currentLoginProvider: LiveData<String> = _currentLoginProvider

  private val _loginFinishEvent = MutableLiveData<Boolean>()
  val loginFinishEvent: LiveData<Boolean> = _loginFinishEvent

  init {
    viewModelScope.launch {
      val config = remarkApi.getConfig()

      @Suppress("MagicString")
      val loginUiItems = loginItemUiMapper.map(config.authProviders.filter { it != "google" })
      _currentLoginProvider.postValue(loginUiItems.first().url)
      _loginUiItem.postValue(loginUiItems)
    }
  }

  fun selectLoginItem(provider: LoginUiItem) {
    _currentLoginProvider.postValue(provider.url)
  }

  fun cookiesChange(cookies: String) {
    viewModelScope.launch {
      if (remarkApi.saveByCookies(cookies)) {
        _loginFinishEvent.postValue(true)
      }
    }
  }
}
