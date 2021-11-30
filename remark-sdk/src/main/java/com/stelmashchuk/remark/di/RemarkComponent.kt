package com.stelmashchuk.remark.di

import android.annotation.SuppressLint
import android.content.Context
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.stelmashchuk.remark.RemarkSettings
import com.stelmashchuk.remark.api.RemarkApi
import com.stelmashchuk.remark.api.comment.CommentRoot
import com.stelmashchuk.remark.feature.auth.ui.screen.AuthProvidersUiMapper
import com.stelmashchuk.remark.feature.delete.DeleteAvailableChecker
import com.stelmashchuk.remark.feature.delete.DeleteViewModel
import com.stelmashchuk.remark.feature.post.PostCommentViewModel
import com.stelmashchuk.remark.os.OsDateTime
import com.stelmashchuk.remark.os.OsStorageImpl

@SuppressLint("StaticFieldLeak")
public object RemarkComponent {

  lateinit var context: Context
  lateinit var remarkSettings: RemarkSettings

  internal val api: RemarkApi by lazy { RemarkApi(remarkSettings.siteId, remarkSettings.baseUrl, OsStorageImpl(BinaryPreferencesBuilder(context).build())) }

  public fun init(context: Context, remarkSettings: RemarkSettings) {
    this.context = context
    this.remarkSettings = remarkSettings
  }

  internal fun authProvidersUiMapper(): AuthProvidersUiMapper {
    return AuthProvidersUiMapper(
        remarkSettings
    )
  }

  internal fun deleteViewModel(commentId: String, postUrl: String): DeleteViewModel {
    return DeleteViewModel(
        commentId = commentId,
        deleteCommentUseCase = api.useCases.getDeleteCommentUseCase(postUrl),
        deleteAvailableChecker = DeleteAvailableChecker(
            configRepository = api.configRepository,
            osDateTime = OsDateTime(),
        ),
    )
  }

  internal fun postCommentViewModel(commentRoot: CommentRoot): PostCommentViewModel {
    return PostCommentViewModel(commentRoot, api.useCases.getPostCommentUseCase(commentRoot.postUrl))
  }
}
