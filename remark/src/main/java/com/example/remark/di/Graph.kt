package com.example.remark.di

import com.example.remark.RemarkSettings
import com.example.remark.data.RemarkService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

internal object Graph {

  val remarkService: RemarkService by lazy {
    Retrofit.Builder()
        .baseUrl(RemarkSettings.BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RemarkService::class.java)
  }

  val json: Json = Json {
    ignoreUnknownKeys = true
  }

}