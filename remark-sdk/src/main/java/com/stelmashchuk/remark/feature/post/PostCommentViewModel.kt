package com.stelmashchuk.remark.feature.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class PostCommentViewModel(
    private val postComment: PostComment,
    private val postCommentStorage: PostCommentStorage,
) : ViewModel() {

  internal val text: StateFlow<String> = flow {
    emitAll(postCommentStorage.flowText())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

  internal val isIconVisible: StateFlow<Boolean> =
      text.map { it.isNotBlank() }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

  fun postComment() {
    viewModelScope.launch {
      postComment.postComment()
    }
  }

  fun updateText(newText: String) {
    viewModelScope.launch {
      postCommentStorage.updateText(newText)
    }
  }
}
