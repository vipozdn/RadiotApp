package com.stelmashchuk.remark.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.stelmashchuk.remark.api.comment.CommentDataControllerProvider
import com.stelmashchuk.remark.api.comment.CommentService
import com.stelmashchuk.remark.api.comment.CommentTimeMapper
import com.stelmashchuk.remark.api.config.ConfigRepository
import com.stelmashchuk.remark.api.user.CredentialCreator
import com.stelmashchuk.remark.api.user.RemarkCredentials
import com.stelmashchuk.remark.api.user.UserRepository
import com.stelmashchuk.remark.api.user.UserService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

public interface SystemStorage {
  fun putString(key: String, value: String)
  fun putStrings(values: Map<String, String>)
  fun getString(key: String): String
  fun onValueChanges(onChange: () -> Unit)
}

public class RemarkApi(
    siteId: String,
    baseUrl: String,
    systemStorage: SystemStorage,
) {

  private val commentService: CommentService by lazy {
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(RemarkInterceptor(userRepository, siteId))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(CommentService::class.java)
  }

  private val userService: UserService by lazy {
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(UserService::class.java)
  }

  private val json: Json = Json {
    ignoreUnknownKeys = true
  }

  public val userRepository: UserRepository by lazy {
    UserRepository(systemStorage, CredentialCreator(), userService)
  }

  public val configRepository: ConfigRepository by lazy {
    ConfigRepository(commentService)
  }

  public val commentDataControllerProvider: CommentDataControllerProvider by lazy {
    CommentDataControllerProvider(commentService, siteId, CommentTimeMapper(), userRepository)
  }

  public suspend fun tryLogin(cookies: String): Boolean {
    return userRepository.loginUser(cookies).isSuccess
  }

  public fun addLoginStateListener(onLoginChange: (RemarkCredentials) -> Unit) {
    return userRepository.addListener(onLoginChange)
  }
}
