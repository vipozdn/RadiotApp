package com.example.radio_t.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.radio_t.presentation.episodes.Episode
import com.example.remark.feature.comments.CommentWidget

@Composable
fun DetailView(podcastNumber: Long?) {
  if (podcastNumber == null) {
    Text(text = "Error")
  } else {
    PodcastContent(podcastNumber)
  }
}

@Composable
private fun PodcastContent(podcastNumber: Long) {
  val viewModel = viewModel(DetailsViewModel::class.java)
  viewModel.loadPodcast(podcastNumber)
  val podcast by viewModel.podcast.observeAsState()
  podcast?.let {
    Column(modifier = Modifier.background(Color.White)) {
      Episode(podcast = it)
      CommentWidget(postUrl = it.url)
    }
  }
}