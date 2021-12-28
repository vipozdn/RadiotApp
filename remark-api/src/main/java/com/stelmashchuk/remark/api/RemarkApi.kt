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
import com.stelmashchuk.remark.api.user.UserRepository
import com.stelmashchuk.remark.api.user.UserService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.ConcurrentHashMap

public interface SystemStorage {
  public fun putString(key: String, value: String)
  public fun putStrings(values: Map<String, String>)
  public fun getString(key: String): String
  public fun onValueChanges(onChange: () -> Unit)
}

public class RemarkApiFactory internal constructor(
    private val siteId: String,
    private val userRepository: UserRepository,
    private val commentService: CommentService,
    private val commentTimeMapper: CommentTimeMapper,
) {

  private val storagesMap = ConcurrentHashMap<String, CommentStorage>()

  private fun commentMapper(): CommentMapper {
    return CommentMapper(commentTimeMapper, userRepository)
  }

  public fun getDataController(postUrl: String): CommentDataController {
    val storage = getStorage(postUrl)
    return CommentDataController(postUrl, commentService, commentMapper(), storage)
  }

  public fun getPostCommentUseCase(postUrl: String): PostCommentUseCase {
    val storage = getStorage(postUrl)
    return PostCommentUseCase(storage, commentService, commentMapper(), siteId, postUrl)
  }

  public fun getDeleteCommentUseCase(postUrl: String): DeleteCommentUseCase {
    val storage = getStorage(postUrl)
    return DeleteCommentUseCase(storage, commentService, postUrl)
  }

  public fun getStorage(postUrl: String): CommentStorage {
    return storagesMap.getOrPut(postUrl) {
      CommentStorage()
    }
  }
}

public class RemarkApi(
    private val remarkSettings: RemarkSettings,
    private val systemStorage: SystemStorage,
    private val okHttpClient: OkHttpClient = OkHttpClient(),
) {

  internal val json: Json = Json {
    ignoreUnknownKeys = true
  }

  private val converterFactory = json.asConverterFactory("application/json".toMediaType())

  private val commentService: CommentService by lazy {
    Retrofit.Builder()
        .baseUrl(remarkSettings.baseUrl)
        .client(
            okHttpClient.newBuilder()
                .addInterceptor(RemarkInterceptor(userRepository, remarkSettings.siteId))
                .build()
        )
        .addConverterFactory(converterFactory)
        .build()
        .create(CommentService::class.java)
  }

  private val userService: UserService by lazy {
    Retrofit.Builder()
        .baseUrl(remarkSettings.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build()
        .create(UserService::class.java)
  }

  public val userRepository: UserRepository by lazy {
    UserRepository(systemStorage, CredentialCreator(), userService)
  }

  public val configRepository: ConfigRepository by lazy {
    ConfigRepository(commentService)
  }

  public fun addLoginStateListener(onLoginChange: (Boolean) -> Unit) {
    return userRepository.addListener {
      onLoginChange(it.isValid())
    }
  }

  public val remarkApiFactory: RemarkApiFactory by lazy {
    RemarkApiFactory(siteId = remarkSettings.siteId, userRepository = userRepository, commentService = commentService, commentTimeMapper = CommentTimeMapper())
  }
}
