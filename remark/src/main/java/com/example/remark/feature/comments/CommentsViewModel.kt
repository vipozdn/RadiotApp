package com.example.remark.feature.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remark.data.ifSuccess
import com.example.remark.data.pojo.VoteType
import com.example.remark.data.repositories.CommentRepository
import com.example.remark.di.Graph
import com.example.remark.feature.comments.mappers.CommentUiMapper
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val commentUiMapper: CommentUiMapper = CommentUiMapper(),
    private val commentRepository: CommentRepository = CommentRepository(Graph.remarkService),
) : ViewModel() {

  private val _commentsLiveData = MutableLiveData<List<CommentUiModel>>()
  val commentsLiveData: LiveData<List<CommentUiModel>> = _commentsLiveData

  private lateinit var postUrl: String

  fun start(postUrl: String) {
    this.postUrl = postUrl
    viewModelScope.launch {
      val comments = commentRepository.getComments(postUrl = postUrl)
      comments.ifSuccess {
        _commentsLiveData.postValue(commentUiMapper.map(it))
      }
    }
  }

  fun vote(commentId: String, voteType: VoteType) {
    viewModelScope.launch {
      val comments = commentRepository.vote(commentId, postUrl, voteType)
      _commentsLiveData.postValue(commentUiMapper.map(comments))
    }
  }
}
