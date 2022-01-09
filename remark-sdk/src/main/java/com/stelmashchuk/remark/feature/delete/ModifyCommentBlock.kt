package com.stelmashchuk.remark.feature.delete

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.di.RemarkComponent
import com.stelmashchuk.remark.feature.comments.CommentUiModel

@Composable
internal fun ModifyCommentBlock(comment: CommentUiModel, postUrl: String) {
  val viewModel: ModifyCommentViewModel = viewModel(key = comment.commentId.raw, factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return RemarkComponent.modifyViewModel(comment.commentId, postUrl) as T
    }
  })

  val timer: Long? by viewModel.deleteAvailable.collectAsState(initial = null)

  if (timer != null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(onClick = { viewModel.delete() }) {
        Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
      }

      IconButton(onClick = { viewModel.startEditFlow() }) {
        Icon(painter = painterResource(id = R.drawable.ic_edit), contentDescription = "Edit")
      }

      Text(text = timer.toString())
    }
  }
}
