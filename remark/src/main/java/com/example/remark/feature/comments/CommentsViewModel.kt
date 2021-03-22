package com.example.remark.feature.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remark.data.RemarkService
import com.example.remark.data.VoteType
import com.example.remark.di.Graph
import com.example.remark.feature.comments.mappers.CommentUiMapper
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val remarkService: RemarkService = Graph.remarkService,
    private val commentUiMapper: CommentUiMapper = CommentUiMapper(),
) : ViewModel() {

  private val _comments = MutableLiveData<List<CommentUiModel>>()
  val comments: LiveData<List<CommentUiModel>> = _comments

  private lateinit var postUrl: String

  fun start(postUrl: String) {
    this.postUrl = postUrl
    viewModelScope.launch {
      val comments = remarkService.getComments(postUrl = postUrl)
      _comments.postValue(commentUiMapper.map(comments))
    }
  }

  fun vote(commentId: String, voteType: VoteType) {
    viewModelScope.launch {
      remarkService.vote(commentId, postUrl, voteType.backendCode)
    }
  }

}