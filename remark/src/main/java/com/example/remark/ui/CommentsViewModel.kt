package com.example.remark.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remark.data.RemarkService
import com.example.remark.di.Graph
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val remarkService: RemarkService = Graph.remarkService,
    private val commentUiMapper: CommentUiMapper = CommentUiMapper(),
) : ViewModel() {

  private val _comments = MutableLiveData<List<CommentUiModel>>()
  val comments: LiveData<List<CommentUiModel>> = _comments

  fun loadComments(postUrl: String) {
    viewModelScope.launch {
      val comments = remarkService.getComments(postUrl = postUrl)
      _comments.postValue(commentUiMapper.map(comments))
    }
  }

}