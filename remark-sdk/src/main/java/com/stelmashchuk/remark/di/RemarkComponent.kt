package com.stelmashchuk.remark.di

import android.annotation.SuppressLint
import android.content.Context
import com.stelmashchuk.remark.RemarkSettings
import com.stelmashchuk.remark.api.RemarkApi
import com.stelmashchuk.remark.feature.auth.ui.screen.AuthProvidersUiMapper

@SuppressLint("StaticFieldLeak")
public object RemarkComponent {

  lateinit var context: Context
  lateinit var remarkSettings: RemarkSettings

  internal val api: RemarkApi by lazy { RemarkApi(context, remarkSettings.siteId, remarkSettings.baseUrl) }

  public fun init(context: Context, remarkSettings: RemarkSettings) {
    this.context = context
    this.remarkSettings = remarkSettings
  }

  internal fun authProvidersUiMapper(): AuthProvidersUiMapper {
    return AuthProvidersUiMapper(
        remarkSettings
    )
  }
}
