package com.stelmashchuk.remark.feature.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.CommentDataController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeleteViewModel(
    private val commentId: String,
    private val commentDataController: CommentDataController,
) : ViewModel() {

  val isDeleteAvialable: StateFlow<Boolean> = flow<Boolean> {

  }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(1000),
      initialValue = false,
  )

  fun delete() {
    viewModelScope.launch {
      commentDataController.delete(commentId)
    }
  }
}