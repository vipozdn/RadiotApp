package com.stelmashchuk.demo_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.stelmashchuk.remark.api.RemarkSettings
import com.stelmashchuk.remark.di.RemarkComponent
import com.stelmashchuk.remark.feature.root.RemarkView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      RemarkView(postUrl = "https://remark42.com/demo/")
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    RemarkComponent.init(
        context = applicationContext,
        remarkSettings = RemarkSettings("remark", "https://demo.remark42.com/"),
        okHttpClient = okHttpClient,
    )
  }
}
