package com.stelmashchuk.remark.feature.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.CommentDataController
import com.stelmashchuk.remark.api.CommentRoot
import com.stelmashchuk.remark.api.RemarkError
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import com.stelmashchuk.remark.feature.root.SnackBarBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class CommentUiState {
  object Empty : CommentUiState()
  object Loading : CommentUiState()
  data class Data(val data: FullCommentsUiModel) : CommentUiState()
}

class CommentViewModel(
    private val commentRoot: CommentRoot,
    private val commentUiMapper: CommentUiMapper,
    private val commentDataController: CommentDataController,
) : ViewModel() {

  private val _comments = MutableStateFlow<CommentUiState>(CommentUiState.Empty)
  val comments: StateFlow<CommentUiState> = _comments

  init {
    _comments.value = CommentUiState.Loading
    viewModelScope.launch {
      commentDataController.observeComments(commentRoot)
          .collectLatest {
            if (it.comments.isEmpty()) {
              _comments.value = CommentUiState.Empty
            } else {
              _comments.value = CommentUiState.Data(commentUiMapper.mapOneLevel(it))
            }
          }
    }
  }

  fun vote(event: CommentViewEvent.Vote) {
    viewModelScope.launch {
      when (commentDataController.vote(event.commentId, commentRoot.postUrl, event.voteType)) {
        RemarkError.NotAuthUser -> {

        }
        RemarkError.SomethingWentWrong -> {

        }
        RemarkError.TooManyRequests -> {
          SnackBarBus.showSnackBar(R.string.too_many_request)
        }
        null -> {
        }
      }
    }
  }

  fun postComment(event: CommentViewEvent.PostComment) {
    viewModelScope.launch {
      commentDataController.postComment(commentRoot, event.text)
    }
  }
}
