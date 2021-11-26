package com.stelmashchuk.remark.feature.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.CommentDataController
import kotlinx.coroutines.launch

class DeleteViewModel(
    private val commentId: String,
    private val commentDataController: CommentDataController,
) : ViewModel() {

  fun delete() {
    viewModelScope.launch {
      commentDataController.delete(commentId)
    }
  }

}