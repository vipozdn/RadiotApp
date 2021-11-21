package com.stelmashchuk.remark.feature.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.CommentDataController
import com.stelmashchuk.remark.api.CommentRoot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostCommentViewModel(
    private val commentRoot: CommentRoot,
    private val commentDataController: CommentDataController,
) : ViewModel() {

  private val _text = MutableStateFlow("")
  internal val text: StateFlow<String> = _text

  internal val isIconVisible: StateFlow<Boolean> = text.map { it.isNotBlank() }.stateIn(viewModelScope, SharingStarted.Lazily, false)

  fun postComment() {
    viewModelScope.launch {
      val error = commentDataController.postComment(commentRoot, text.value)
      if (error == null) {
        _text.emit("")
      }
    }
  }

  fun updateText(newText: String) {
    viewModelScope.launch {
      _text.emit(newText)
    }
  }
}
