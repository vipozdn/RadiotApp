package com.stelmashchuk.remark.feature.post

import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.api.comment.CommentStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

internal class EditMode(
    private val commentStorage: CommentStorage,
    private val postCommentStorage: PostCommentStorage,
) {

  private val editCommentId = MutableStateFlow<CommentId?>(null)

  suspend fun flowEditCommentId(): StateFlow<CommentId?> {
    return editCommentId.stateIn(GlobalScope)
  }

  suspend fun startEditMode(commentId: CommentId) {
    editCommentId.emit(commentId)
    postCommentStorage.updateText(commentStorage.waitForComment(commentId).text)
  }

  suspend fun closeEditMode() {
    editCommentId.emit(null)
  }

}
