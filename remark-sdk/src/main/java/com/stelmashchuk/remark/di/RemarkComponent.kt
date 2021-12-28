package com.stelmashchuk.remark.di

import android.annotation.SuppressLint
import android.content.Context
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.stelmashchuk.remark.ResourcesRepository
import com.stelmashchuk.remark.api.RemarkApi
import com.stelmashchuk.remark.api.RemarkSettings
import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.api.comment.CommentRoot
import com.stelmashchuk.remark.feature.auth.ui.screen.AuthProvidersUiMapper
import com.stelmashchuk.remark.feature.delete.DeleteAvailableChecker
import com.stelmashchuk.remark.feature.delete.ModifyCommentViewModel
import com.stelmashchuk.remark.feature.post.PostCommentViewModel
import com.stelmashchuk.remark.os.OsDateTime
import com.stelmashchuk.remark.os.OsStorageImpl
import okhttp3.OkHttpClient

@SuppressLint("StaticFieldLeak")
public object RemarkComponent {

  private lateinit var context: Context
  private lateinit var remarkSettings: RemarkSettings
  private lateinit var okHttpClient: OkHttpClient

  internal val api: RemarkApi by lazy { RemarkApi(remarkSettings, OsStorageImpl(BinaryPreferencesBuilder(context).build()), okHttpClient) }

  public fun init(context: Context, remarkSettings: RemarkSettings, okHttpClient: OkHttpClient = OkHttpClient()) {
    this.context = context
    this.remarkSettings = remarkSettings
    this.okHttpClient = okHttpClient
  }

  internal val resourcesRepository: ResourcesRepository by lazy {
    ResourcesRepository(context)
  }

  internal fun authProvidersUiMapper(): AuthProvidersUiMapper {
    return AuthProvidersUiMapper(
        remarkSettings
    )
  }

  internal fun deleteViewModel(commentId: CommentId, postUrl: String): ModifyCommentViewModel {
    return ModifyCommentViewModel(
        commentId = commentId,
        deleteCommentUseCase = api.remarkApiFactory.getDeleteCommentUseCase(postUrl),
        deleteAvailableChecker = DeleteAvailableChecker(
            configRepository = api.configRepository,
            osDateTime = OsDateTime(),
        ),
        commentStorage = api.remarkApiFactory.getStorage(postUrl)
    )
  }

  internal fun postCommentViewModel(commentRoot: CommentRoot): PostCommentViewModel {
    return PostCommentViewModel(commentRoot, api.remarkApiFactory.getPostCommentUseCase(commentRoot.postUrl))
  }
}
