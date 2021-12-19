package com.stelmashchuk.remark.feature.auth.ui.screen

import com.stelmashchuk.remark.api.RemarkSettings
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

internal class AuthProvidersUiMapper(
    private val remarkSettings: RemarkSettings,
) {

  fun map(providers: List<String>): List<LoginUiItem> {
    return providers.map { provider ->
      LoginUiItem(
          provider,
          HttpUrl.Builder()
              .scheme(remarkSettings.baseUrl.toHttpUrl().scheme)
              .host(remarkSettings.baseUrl.toHttpUrl().host)
              .addPathSegments("auth")
              .addPathSegments(provider)
              .addPathSegments("login")
              .addQueryParameter("site", remarkSettings.siteId)
              .addQueryParameter("session", "1")
              .build()
              .toString()
      )
    }
  }
}
