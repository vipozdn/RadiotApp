package com.stelmashchuk.remark.feature.post

import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.api.comment.CommentRoot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

internal class PostCommentStorage(internal val root: CommentRoot) : EditMode {

  private val editCommentId = MutableStateFlow<CommentId?>(null)

  private val text = MutableStateFlow("")

  suspend fun flowText(): StateFlow<String> {
    return text.stateIn(GlobalScope)
  }

  suspend fun flowEditCommentId(): StateFlow<CommentId?> {
    return editCommentId.stateIn(GlobalScope)
  }

  override suspend fun startEditMode(commentId: CommentId) {
    editCommentId.emit(commentId)
  }

  override suspend fun closeEditMode() {
    editCommentId.emit(null)
  }

  suspend fun updateText(newText: String) {
    text.emit(newText)
  }

  suspend fun clear() {
    text.emit("")
    closeEditMode()
  }
}
