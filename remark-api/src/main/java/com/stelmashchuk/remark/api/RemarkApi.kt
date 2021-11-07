package com.stelmashchuk.remark.api

import android.content.Context
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.stelmashchuk.remark.api.network.RemarkInterceptor
import com.stelmashchuk.remark.api.network.RemarkService
import com.stelmashchuk.remark.api.repositories.UserStorage
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

public class RemarkApi(
    context: Context,
    siteId: String,
    baseUrl: String,
) {

  private val remarkService: RemarkService by lazy {
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(RemarkInterceptor(userStorage, siteId))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RemarkService::class.java)
  }

  public val commentDataControllerProvider: CommentDataControllerProvider by lazy {
    CommentDataControllerProvider(remarkService, userStorage)
  }

  public suspend fun getConfig() = remarkService.getConfig()

  public fun saveByCookies(cookies: String): Boolean {
    return userStorage.saveByCookies(cookies)
  }

  private val json: Json = Json {
    ignoreUnknownKeys = true
  }

  public val userStorage: UserStorage by lazy {
    UserStorage(BinaryPreferencesBuilder(context).build())
  }
}
