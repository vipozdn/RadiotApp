package com.stelmashchuk.remark.feature.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.comment.DeleteCommentUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

internal class DeleteViewModel(
    private val commentId: String,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val deleteAvailableChecker: DeleteAvailableChecker,
) : ViewModel() {

  fun deleteAvailable(): Flow<Long?> = flow {
    val comment = deleteCommentUseCase.getCommentById(commentId)
    val delta = deleteAvailableChecker.check(comment)
    if (delta != null) {
      (delta downTo 0).forEach {
        emit(it)
        delay(1000)
      }
    }
  }

  fun delete() {
    viewModelScope.launch {
      deleteCommentUseCase.delete(commentId)
    }
  }
}