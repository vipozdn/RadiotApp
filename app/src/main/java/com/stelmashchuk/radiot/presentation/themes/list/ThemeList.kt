package com.stelmashchuk.radiot.presentation.themes.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ThemeList(openDetails: (Long) -> Unit) {
  val viewModel = viewModel(ThemeListViewModel::class.java)

  val themes by viewModel.themes.observeAsState(emptyList())

  LazyColumn(Modifier.background(Color.Black)) {
    items(themes) {
      Text(modifier = Modifier.clickable {
        openDetails(it.number)
      }, text = it.title, style = MaterialTheme.typography.h4)
    }
  }
}
