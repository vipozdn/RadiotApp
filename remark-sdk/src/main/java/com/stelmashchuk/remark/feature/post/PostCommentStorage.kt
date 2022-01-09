package com.stelmashchuk.remark.feature.post

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

internal class PostCommentStorage {

  private val text = MutableStateFlow("")

  suspend fun flowText(): StateFlow<String> {
    return text.stateIn(GlobalScope)
  }

  suspend fun updateText(newText: String) {
    text.emit(newText)
  }

  suspend fun clear() {
    text.emit("")
  }
}
