package com.stelmashchuk.remark.feature.delete

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.di.RemarkComponent
import com.stelmashchuk.remark.feature.comments.CommentUiModel

@Composable
fun DeleteButton(comment: CommentUiModel, postUrl: String) {
  val viewModel: DeleteViewModel = viewModel(key = comment.commentId, factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return RemarkComponent.deleteViewModel(comment.commentId, postUrl) as T
    }
  })

  if (viewModel.isDeleteAvailable.collectAsState().value == true) {
    IconButton(onClick = { viewModel.delete() }) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
        Text(text = "200")
      }
    }
  }
}
