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

sealed class ViewState<out T> {
  object Loading : ViewState<Nothing>()
  data class Data<T>(val data: T) : ViewState<T>()
}

class CommentsViewModel(
    private val postUrl: String,
    private val commentUiMapper: CommentUiMapper = CommentUiMapper(),
    private val commentRepository: CommentRepository = CommentRepository(Graph.remarkService),
) : ViewModel() {

  private val _commentsLiveData = MutableLiveData<ViewState<List<CommentUiModel>>>()
  val commentsLiveData: LiveData<ViewState<List<CommentUiModel>>> = _commentsLiveData

  init {
    _commentsLiveData.postValue(ViewState.Loading)
    viewModelScope.launch {
      val comments = commentRepository.getComments(postUrl = postUrl)
      comments.onSuccess {
        _commentsLiveData.postValue(ViewState.Data(commentUiMapper.map(it)))
      }
    }
  }

  fun vote(commentId: String, voteType: VoteType) {
    viewModelScope.launch {
      val comments = commentRepository.vote(commentId, postUrl, voteType)
      _commentsLiveData.postValue(ViewState.Data(commentUiMapper.map(comments)))
    }
  }
}
