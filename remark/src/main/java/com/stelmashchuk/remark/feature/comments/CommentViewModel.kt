package com.stelmashchuk.remark.feature.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.new.CommentDataControllerProvider
import com.stelmashchuk.remark.api.new.CommentRoot
import com.stelmashchuk.remark.feature.CommentViewEvent
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommentViewModel(
    private val commentRoot: CommentRoot,
    private val commentDataControllerProvider: CommentDataControllerProvider,
    private val commentUiMapper: CommentUiMapper,
) : ViewModel() {

  private val commentDataController = commentDataControllerProvider.getDataController(commentRoot.postUrl, viewModelScope)

  private val _comments = MutableStateFlow(emptyList<CommentUiModel>())
  val comments: StateFlow<List<CommentUiModel>> = _comments

  init {
    viewModelScope.launch {
      commentDataController.observeComments(commentRoot)
          .collect {
            _comments.value = commentUiMapper.mapOneLevel(it)
          }
    }
  }

  fun vote(event: CommentViewEvent.Vote) {
    viewModelScope.launch {
      commentDataController.vote(event.commentId, commentRoot.postUrl, event.voteType)
    }
  }

  override fun onCleared() {
    super.onCleared()
    commentDataControllerProvider.clean(commentRoot.postUrl)
  }
}
