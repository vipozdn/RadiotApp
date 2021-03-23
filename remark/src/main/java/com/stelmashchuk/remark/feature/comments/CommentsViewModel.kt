package com.stelmashchuk.remark.feature.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.data.onSuccess
import com.stelmashchuk.remark.data.pojo.VoteType
import com.stelmashchuk.remark.data.repositories.CommentRepository
import com.stelmashchuk.remark.di.Graph
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
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
      comments.onSuccess {
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
