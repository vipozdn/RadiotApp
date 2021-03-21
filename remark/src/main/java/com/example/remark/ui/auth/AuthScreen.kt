package com.example.remark.ui.auth

import android.annotation.SuppressLint
import android.view.ViewGroup
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
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen() {
  val viewModel = viewModel(AuthViewModel::class.java)
  val loginItems by viewModel.loginUiItem.observeAsState()
  val currentProvider by viewModel.currentLoginProvider.observeAsState()

  Column {
    loginItems?.let {
      AuthList(it, viewModel)
    }
    currentProvider?.let {
      WebPageScreen(it)
    }
  }
}

@Composable
fun AuthList(loginItems: List<LoginUiItem>, viewModel: AuthViewModel) {
  LazyRow {
    items(loginItems) {
      Button(onClick = { viewModel.selectLoginItem(it) }) {
        Text(text = it.name)
      }
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPageScreen(urlToRender: String) {
  AndroidView(factory = {
    WebView(it).apply {
      layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
      )
      webViewClient = WebViewClient()
      loadUrl(urlToRender)
    }
  }, update = {
    it.loadUrl(urlToRender)
  })
}