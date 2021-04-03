package com.stelmashchuk.remark.feature.auth.ui.screen

import com.stelmashchuk.remark.RemarkSettings
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

class AuthProvidersUiMapper {

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
              .addQueryParameter("site", RemarkSettings.siteId)
              .addQueryParameter("session", "1")
              .build()
              .toString()
      )
    }
  }
}
