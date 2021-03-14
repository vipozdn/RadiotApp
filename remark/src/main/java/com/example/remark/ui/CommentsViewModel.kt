package com.example.remark.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remark.data.RemarkService
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val remarkService: RemarkService,
) : ViewModel() {

  fun loadComments(postUrl: String) {
    viewModelScope.launch {
      val comments = remarkService.getComments(postUrl = postUrl)
    }
  }

}