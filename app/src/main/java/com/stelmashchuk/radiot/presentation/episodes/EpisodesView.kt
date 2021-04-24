package com.stelmashchuk.radiot.presentation.episodes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EpisodesView(openDetails: (Long) -> Unit) {
  val viewModel = viewModel(EpisodesViewModel::class.java)

  val posts by viewModel.post.observeAsState(emptyList())

  LazyColumn(Modifier.background(Color.Black)) {
    items(posts) { podcast ->
      Episode(podcast, openDetails)
    }
  }
}

@Composable
fun Episode(podcast: EpisodeUiModel, openDetails: (Long) -> Unit = {}) {
  Column(modifier = Modifier
      .background(Color.Black)
      .padding(8.dp)
      .fillMaxWidth()
      .clickable { openDetails(podcast.number) }) {
    Text(text = podcast.title, style = MaterialTheme.typography.h4)
    Text(text = podcast.topics, style = MaterialTheme.typography.subtitle1)
  }
}
