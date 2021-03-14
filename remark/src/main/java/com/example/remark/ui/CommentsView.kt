package com.example.remark.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel

data class CommentUiModel(
    val userName: String,
    val text: String,
    val level: Long,
)

@Composable
fun CommentView(postUrl: String) {
  val viewModel: CommentsViewModel = viewModel(CommentsViewModel::class.java)
  viewModel.loadComments(postUrl)

  val data by viewModel.comments.observeAsState()

  data?.let {
    LazyColumn {
      items(it) { comment ->
        Column {
          Text(text = "name ${comment.userName}")
          Text(text = "body ${comment.text}")
        }
      }
    }
  }
}
