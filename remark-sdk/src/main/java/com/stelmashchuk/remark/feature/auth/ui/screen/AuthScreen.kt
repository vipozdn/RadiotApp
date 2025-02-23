package com.stelmashchuk.remark.feature.auth.ui.screen

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stelmashchuk.remark.di.RemarkComponent

@Composable
internal fun AuthScreen(onLoginFinish: () -> Unit) {
  val viewModel: AuthViewModel = viewModel(factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return AuthViewModel(
          configRepository = RemarkComponent.api.configRepository,
          loginItemUiMapper = RemarkComponent.authProvidersUiMapper(),
          userRepository = RemarkComponent.api.userRepository,
      ) as T
    }
  })
  val loginItems by viewModel.loginUiItem.observeAsState()
  val currentProvider by viewModel.currentLoginProvider.observeAsState()

  val loginFinish by viewModel.loginFinishEvent.observeAsState()

  if (loginFinish == true) {
    onLoginFinish()
  }

  Column {
    loginItems?.let {
      AuthList(it, viewModel)
    }
    currentProvider?.let {
      WebPageScreen(it, viewModel::cookiesChange)
    }
  }
}

@Composable
internal fun AuthList(loginItems: List<LoginUiItem>, viewModel: AuthViewModel) {
  LazyRow {
    items(loginItems) { loginItem ->
      Button(onClick = { viewModel.selectLoginItem(loginItem) }) {
        Text(text = loginItem.name)
      }
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun WebPageScreen(urlToRender: String, onCookieChange: (String) -> Unit) {
  AndroidView(factory = { context ->
    val webView = WebView(context)
    CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
    CookieManager.getInstance().acceptThirdPartyCookies(webView)
    CookieManager.getInstance().setAcceptCookie(true)
    webView.apply {
      layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
      )
      this.settings.apply {
        @SuppressLint("SetJavaScriptEnabled")
        javaScriptEnabled = true
        javaScriptCanOpenWindowsAutomatically = true
      }

      this.webChromeClient = WebChromeClient()
      webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
          super.onPageFinished(view, url)
          CookieManager.getInstance().getCookie(url)?.let(onCookieChange)
        }
      }
      loadUrl(urlToRender)
    }
    webView
  }, update = {
    it.loadUrl(urlToRender)
  })
}
