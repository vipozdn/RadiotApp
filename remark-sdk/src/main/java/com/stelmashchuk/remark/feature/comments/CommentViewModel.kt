package com.stelmashchuk.remark.feature.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.api.comment.CommentDataController
import com.stelmashchuk.remark.api.comment.CommentRoot
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal sealed class CommentUiState {
  object Empty : CommentUiState()
  object Loading : CommentUiState()
  data class Data(val data: FullCommentsUiModel) : CommentUiState()
}

internal class CommentViewModel(
    private val commentRoot: CommentRoot,
    private val commentUiMapper: CommentUiMapper,
    private val commentDataController: CommentDataController,
) : ViewModel() {

  val comments: StateFlow<CommentUiState> = flow {
    emitAll(
        commentDataController.observeComments(commentRoot)
            .map { fullCommentInfo ->
              if (fullCommentInfo.comments.isEmpty() && fullCommentInfo.rootComment == null) {
                CommentUiState.Empty
              } else {
                CommentUiState.Data(commentUiMapper.mapOneLevel(fullCommentInfo))
              }
            }
    )
  }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CommentUiState.Loading)

}
