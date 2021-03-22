package com.example.remark.di

import android.annotation.SuppressLint
import android.content.Context
import com.example.remark.RemarkSettings
import com.example.remark.data.RemarkService
import com.example.remark.data.repositories.UserStorage
import com.example.remark.data.interceptors.RemarkInterceptor
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@SuppressLint("StaticFieldLeak")
public object Graph {

  lateinit var context: Context

  fun init(context: Context) {
    this.context = context
  }

  val remarkService: RemarkService by lazy {
    Retrofit.Builder()
        .baseUrl(RemarkSettings.baseUrl)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(RemarkInterceptor(userStorage))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RemarkService::class.java)
  }

  private val json: Json = Json {
    ignoreUnknownKeys = true
  }

  val userStorage: UserStorage by lazy {
    UserStorage(BinaryPreferencesBuilder(context).build())
  }
}
