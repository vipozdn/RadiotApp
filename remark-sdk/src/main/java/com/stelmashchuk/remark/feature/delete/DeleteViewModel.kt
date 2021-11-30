package com.stelmashchuk.remark.feature.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.comment.DeleteCommentUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class DeleteViewModel(
    private val commentId: String,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val deleteAvailableChecker: DeleteAvailableChecker,
) : ViewModel() {

  val isDeleteAvailable: StateFlow<Boolean?> = flow {
    emitAll(deleteCommentUseCase.observeCommentDeleteAvailable(commentId = commentId)
        .map { deleteAvailableChecker.check(it) })
  }
      .onEach {

      }
      .stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(1000),
          initialValue = null,
      )

  fun delete() {
    viewModelScope.launch {
      deleteCommentUseCase.delete(commentId)
    }
  }
}