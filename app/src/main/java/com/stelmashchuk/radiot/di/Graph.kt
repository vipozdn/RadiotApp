package com.stelmashchuk.radiot.di

import com.stelmashchuk.radiot.data.PodcastRepository
import com.stelmashchuk.radiot.data.RadiotService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.stelmashchuk.radiot.data.ThemesRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object Graph {

  private const val BASE_URL = "https://radio-t.com/"

  private val radiotService: RadiotService by lazy {
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RadiotService::class.java)
  }

  val podcastRepository: PodcastRepository by lazy {
    PodcastRepository(radiotService)
  }

  val themeRepository: ThemesRepository by lazy {
    ThemesRepository(radiotService)
  }

  val json: Json = Json {
    ignoreUnknownKeys = true
  }
}
