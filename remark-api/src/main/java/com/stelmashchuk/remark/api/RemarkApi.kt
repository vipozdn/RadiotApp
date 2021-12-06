package com.stelmashchuk.remark.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.stelmashchuk.remark.api.comment.CommentDataController
import com.stelmashchuk.remark.api.comment.CommentMapper
import com.stelmashchuk.remark.api.comment.CommentService
import com.stelmashchuk.remark.api.comment.CommentStorage
import com.stelmashchuk.remark.api.comment.CommentTimeMapper
import com.stelmashchuk.remark.api.comment.DeleteCommentUseCase
import com.stelmashchuk.remark.api.comment.PostCommentUseCase
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

public class UseCases internal constructor(
    private val siteId: String,
    private val userRepository: UserRepository,
    private val commentService: CommentService,
    private val commentTimeMapper: CommentTimeMapper,
) {

  private val storagesMap = HashMap<String, CommentStorage>()

  private fun commentMapper(): CommentMapper {
    return CommentMapper(commentTimeMapper, userRepository)
  }

  public fun getDataController(postUrl: String): CommentDataController {
    val storage = storagesMap.getOrPut(postUrl) {
      CommentStorage()
    }
    return CommentDataController(postUrl, commentService, commentMapper(), storage)
  }

  public fun getPostCommentUseCase(postUrl: String): PostCommentUseCase {
    val storage = storagesMap.getOrPut(postUrl) {
      CommentStorage()
    }
    return PostCommentUseCase(storage, commentService, commentMapper(), siteId, postUrl)
  }

  public fun getDeleteCommentUseCase(postUrl: String): DeleteCommentUseCase {
    val storage = storagesMap.getOrPut(postUrl) {
      CommentStorage()
    }

    return DeleteCommentUseCase(storage, commentService, postUrl)
  }

}

public class RemarkApi(
    private val siteId: String,
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

  public fun addLoginStateListener(onLoginChange: (RemarkCredentials) -> Unit) {
    return userRepository.addListener(onLoginChange)
  }

  public val useCases: UseCases by lazy {
    UseCases(siteId = siteId, userRepository = userRepository, commentService = commentService, commentTimeMapper = CommentTimeMapper())
  }
}
