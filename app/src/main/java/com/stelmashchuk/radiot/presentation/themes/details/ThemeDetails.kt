package com.stelmashchuk.radiot.presentation.themes.details

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stelmashchuk.radiot.presentation.common.DetailsScreen
import com.stelmashchuk.remark.feature.RemarkView
import com.stelmashchuk.remark.feature.comments.OneCommentView

@Composable
fun ThemeDetails(number: Long?) {
  if (number == null) {
    Text(text = "Error")
  } else {
    ThemeContent(number)
  }
}

@Composable
fun ThemeContent(number: Long) {
  val viewModel = viewModel<ThemeViewModel>(factory = ThemeViewModelFactory(number))

  val data by viewModel.data.observeAsState()
  data?.let { theme ->
    DetailsScreen(name = theme.title) {
      RemarkView(postUrl = theme.url)
    }
  }
}
