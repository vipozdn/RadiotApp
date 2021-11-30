package com.stelmashchuk.remark.feature.vote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.CommentDataController
import com.stelmashchuk.remark.api.RemarkError
import com.stelmashchuk.remark.api.config.VoteType
import com.stelmashchuk.remark.feature.root.SnackBarBus
import kotlinx.coroutines.launch

class ScoreViewModel(
    private val commentId: String,
    private val commentDataController: CommentDataController,
) : ViewModel() {

  fun vote(voteType: VoteType) {
    viewModelScope.launch {
      when (commentDataController.vote(commentId, voteType)) {
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
}
