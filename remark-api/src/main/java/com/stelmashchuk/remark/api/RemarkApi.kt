package com.stelmashchuk.remark.api

import android.content.Context
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.stelmashchuk.remark.api.network.RemarkInterceptor
import com.stelmashchuk.remark.api.network.RemarkService
import com.stelmashchuk.remark.api.pojo.Config
import com.stelmashchuk.remark.api.repositories.CredentialCreator
import com.stelmashchuk.remark.api.repositories.RemarkCredentials
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

  private val json: Json = Json {
    ignoreUnknownKeys = true
  }

  private val userStorage: UserStorage by lazy {
    UserStorage(BinaryPreferencesBuilder(context).build(), CredentialCreator(), remarkService)
  }

  public val commentDataControllerProvider: CommentDataControllerProvider by lazy {
    CommentDataControllerProvider(remarkService, siteId)
  }

  public suspend fun getConfig(): Config = remarkService.getConfig()

  public suspend fun saveByCookies(cookies: String): Boolean {
    return userStorage.saveByCookies(cookies)
  }

  public fun addLoginStateListener(onLoginChange: (RemarkCredentials) -> Unit) {
    return userStorage.addListener(onLoginChange)
  }
}
