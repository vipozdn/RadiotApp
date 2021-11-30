package com.stelmashchuk.remark.di

import android.annotation.SuppressLint
import android.content.Context
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.stelmashchuk.remark.RemarkSettings
import com.stelmashchuk.remark.api.RemarkApi
import com.stelmashchuk.remark.feature.auth.ui.screen.AuthProvidersUiMapper
import com.stelmashchuk.remark.os.OsStorageImpl

@SuppressLint("StaticFieldLeak")
public object RemarkComponent {

  lateinit var context: Context
  lateinit var remarkSettings: RemarkSettings

  internal val api: RemarkApi by lazy { RemarkApi(remarkSettings.siteId, remarkSettings.baseUrl, OsStorageImpl(BinaryPreferencesBuilder(context).build())) }

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
