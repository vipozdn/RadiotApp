package com.stelmashchuk.remark.feature.post

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stelmashchuk.remark.R
import com.stelmashchuk.remark.api.comment.CommentRoot
import com.stelmashchuk.remark.di.RemarkComponent

@Composable
fun WriteCommentView(commentRoot: CommentRoot) {
  val viewModel: PostCommentViewModel = viewModel(key = commentRoot.toString(), factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return PostCommentViewModel(commentRoot, RemarkComponent.api.commentDataControllerProvider.getDataController(commentRoot.postUrl)) as T
    }
  })

  TextField(
      value = viewModel.text.collectAsState().value,
      modifier = Modifier.fillMaxWidth(),
      onValueChange = {
        viewModel.updateText(it)
      },
      placeholder = {
        Text(text = stringResource(id = R.string.leave_comment))
      },
      trailingIcon = {
        if (viewModel.isIconVisible.collectAsState().value) {
          IconButton(onClick = { viewModel.postComment() }) {
            Icon(painter = painterResource(id = R.drawable.ic_send), contentDescription = "send comment")
          }
        }
      },
  )
}
