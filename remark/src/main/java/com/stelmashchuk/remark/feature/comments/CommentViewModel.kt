package com.stelmashchuk.remark.feature.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.CommentDataControllerProvider
import com.stelmashchuk.remark.api.CommentRoot
import com.stelmashchuk.remark.feature.CommentViewEvent
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class CommentUiState {
  object Empty : CommentUiState()
  object Loading : CommentUiState()
  data class Data(val data: FullCommentsUiModel) : CommentUiState()
}

class CommentViewModel(
    private val commentRoot: CommentRoot,
    private val commentDataControllerProvider: CommentDataControllerProvider,
    private val commentUiMapper: CommentUiMapper,
) : ViewModel() {

  private val commentDataController = commentDataControllerProvider.getDataController(commentRoot.postUrl, viewModelScope)

  private val _comments = MutableStateFlow<CommentUiState>(CommentUiState.Empty)
  val comments: StateFlow<CommentUiState> = _comments

  init {
    viewModelScope.launch {
      commentDataController.observeComments(commentRoot)
          .collect {
            _comments.value = CommentUiState.Data(commentUiMapper.mapOneLevel(it))
          }
    }
  }

  fun vote(event: CommentViewEvent.Vote) {
    viewModelScope.launch {
      commentDataController.vote(event.commentId, commentRoot.postUrl, event.voteType)
    }
  }
}
