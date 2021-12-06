package com.stelmashchuk.remark.feature.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.comment.DeleteCommentUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class DeleteViewModel(
    private val commentId: String,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val deleteAvailableChecker: DeleteAvailableChecker,
) : ViewModel() {

  val deleteAvailable: StateFlow<Long?> by lazy {
    flow {
      val comment = deleteCommentUseCase.getCommentById(commentId)
      val delta = deleteAvailableChecker.check(comment)
      if (delta != null) {
        (delta downTo 1).forEach {
          emit(it)
          delay(1000)
        }
        emit(null)
      }
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
  }

  fun delete() {
    viewModelScope.launch {
      deleteCommentUseCase.delete(commentId)
    }
  }
}