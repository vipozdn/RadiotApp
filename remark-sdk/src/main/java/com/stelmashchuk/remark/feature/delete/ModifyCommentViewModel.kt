package com.stelmashchuk.remark.feature.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.api.comment.CommentStorage
import com.stelmashchuk.remark.api.comment.DeleteCommentUseCase
import com.stelmashchuk.remark.feature.post.EditMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ModifyCommentViewModel(
    private val commentId: CommentId,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val modifyAvailableChecker: ModifyAvailableChecker,
    private val commentStorage: CommentStorage,
    private val editMode: EditMode,
) : ViewModel() {

  val deleteAvailable: StateFlow<Long?> by lazy {
    flow {
      val comment = commentStorage.waitForComment(commentId)
      val delta = modifyAvailableChecker.check(comment)
      if (delta != null) {
        (delta downTo 1).forEach {
          emit(it)
          delay(1000)
        }
        emit(null)
      }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
  }

  fun delete() {
    viewModelScope.launch {
      deleteCommentUseCase.delete(commentId)
    }
  }

  fun startEditFlow() {
    viewModelScope.launch {
      editMode.startEditMode(commentId)
    }
  }
}
