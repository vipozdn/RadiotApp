package com.stelmashchuk.remark.feature.comments

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.common.SingleLiveEvent
import com.stelmashchuk.remark.data.onFailure
import com.stelmashchuk.remark.data.onSuccess
import com.stelmashchuk.remark.data.pojo.VoteType
import com.stelmashchuk.remark.data.repositories.CommentRepository
import com.stelmashchuk.remark.data.repositories.NotAuthUser
import com.stelmashchuk.remark.data.repositories.TooManyRequests
import com.stelmashchuk.remark.di.Graph
import com.stelmashchuk.remark.feature.NavigationActions
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import kotlinx.coroutines.launch

sealed class ViewState<out T> {
  object Loading : ViewState<Nothing>()
  data class Data<T>(val data: T) : ViewState<T>()
}

data class InfoMessage(@StringRes val id: Int)

class CommentsViewModel(
    private val postUrl: String,
    private val navigationActions: NavigationActions,
    private val commentUiMapper: CommentUiMapper = CommentUiMapper(),
    private val commentRepository: CommentRepository = CommentRepository(Graph.remarkService, Graph.userStorage),
) : ViewModel() {

  private val _commentsLiveData = MutableLiveData<ViewState<List<CommentUiModel>>>()
  val commentsLiveData: LiveData<ViewState<List<CommentUiModel>>> = _commentsLiveData

  val messageLiveData = SingleLiveEvent<InfoMessage>()

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
      val result = commentRepository.vote(commentId, postUrl, voteType)
      result.onSuccess { comments ->
        _commentsLiveData.postValue(ViewState.Data(commentUiMapper.map(comments)))
      }
      result.onFailure {
        when (it) {
          is NotAuthUser -> {
            navigationActions.openLogin()
          }
          is TooManyRequests -> {
            messageLiveData.postValue(InfoMessage(R.string.too_many_request))
          }
        }
      }
    }
  }
}
