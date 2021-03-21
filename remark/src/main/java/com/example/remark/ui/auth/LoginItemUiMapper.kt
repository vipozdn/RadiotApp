package com.example.remark.ui.auth

import com.example.remark.RemarkSettings
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

class LoginItemUiMapper {

  fun map(providers: List<String>): List<LoginUiItem> {
    return providers.map { provider ->
      LoginUiItem(
          provider,
          HttpUrl.Builder()
              .scheme(RemarkSettings.baseUrl.toHttpUrl().scheme)
              .host(RemarkSettings.baseUrl.toHttpUrl().host)
              .addPathSegments("auth")
              .addPathSegments(provider)
              .addPathSegments("login")
              .addQueryParameter("from", RemarkSettings.baseUrl)
              .addQueryParameter("site", RemarkSettings.siteId)
              .addQueryParameter("session", "1")
              .build()
              .toString()
      )
    }
  }
}