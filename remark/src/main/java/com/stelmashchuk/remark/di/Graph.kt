package com.stelmashchuk.remark.di

import android.annotation.SuppressLint
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.stelmashchuk.remark.RemarkSettings
import com.stelmashchuk.remark.api.RemarkApi

@SuppressLint("StaticFieldLeak")
public object Graph {

  lateinit var context: Context

  internal val api: RemarkApi by lazy { RemarkApi(context, RemarkSettings.siteId, RemarkSettings.baseUrl) }

  fun init(context: Context) {
    this.context = context
    AndroidThreeTen.init(context)
  }
}
