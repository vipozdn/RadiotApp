package com.stelmashchuk.remark.feature.comments

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.common.SingleLiveEvent
import com.stelmashchuk.remark.api.onFailure
import com.stelmashchuk.remark.api.onSuccess
import com.stelmashchuk.remark.api.pojo.VoteType
import com.stelmashchuk.remark.api.repositories.CommentRepository
import com.stelmashchuk.remark.api.repositories.NotAuthUser
import com.stelmashchuk.remark.api.repositories.TooManyRequests
import com.stelmashchuk.remark.di.Graph
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import kotlinx.coroutines.launch

sealed class ViewState<out T> {
  object Loading : ViewState<Nothing>()
  data class Data<T>(val data: T) : ViewState<T>()
}

data class InfoMessage(@StringRes val id: Int)

sealed class CommentRoot {
  data class Post(
      val postUrl: String,
  ) : CommentRoot()

  data class Comment(
      val commentId: String,
  ) : CommentRoot()
}

class CommentsViewModel(
    private val commentRoot: CommentRoot,
    private val commentUiMapper: CommentUiMapper = CommentUiMapper(),
    private val commentRepository: com.stelmashchuk.remark.api.repositories.CommentRepository = com.stelmashchuk.remark.api.repositories.CommentRepository(Graph.remarkService, Graph.userStorage),
) : ViewModel() {

  private val _commentsLiveData = MutableLiveData<ViewState<List<CommentUiModel>>>()
  val commentsLiveData: LiveData<ViewState<List<CommentUiModel>>> = _commentsLiveData

  val messageLiveData = SingleLiveEvent<InfoMessage>()

  init {
    _commentsLiveData.postValue(ViewState.Loading)
    viewModelScope.launch {
      when (commentRoot) {
        is CommentRoot.Comment -> {
          val comments = commentRepository.getReplayByComment(rootId = commentRoot.commentId)
          _commentsLiveData.postValue(ViewState.Data(commentUiMapper.mapOneLevel(comments)))
        }
        is CommentRoot.Post -> {
          val comments = commentRepository.getComments(postUrl = commentRoot.postUrl)
          comments.onSuccess {
            _commentsLiveData.postValue(ViewState.Data(commentUiMapper.mapOneLevel(it.comments)))
          }
        }
      }

    }
  }

  fun vote(commentId: String, voteType: com.stelmashchuk.remark.api.pojo.VoteType) {
    val postUrl = (commentRoot as? CommentRoot.Post)?.postUrl ?: throw Exception()
    viewModelScope.launch {
      val result = commentRepository.vote(commentId, postUrl, voteType)
      result.onSuccess { comments ->
        _commentsLiveData.postValue(ViewState.Data(commentUiMapper.map(comments)))
      }
      result.onFailure {
        when (it) {
          is com.stelmashchuk.remark.api.repositories.NotAuthUser -> {
            //navigationActions.openLogin()
          }
          is com.stelmashchuk.remark.api.repositories.TooManyRequests -> {
            messageLiveData.postValue(InfoMessage(R.string.too_many_request))
          }
        }
      }
    }
  }
}
