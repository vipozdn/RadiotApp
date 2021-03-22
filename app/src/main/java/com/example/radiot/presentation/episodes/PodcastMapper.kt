package com.example.radiot.presentation.episodes

import com.example.radiot.data.Podcast

class PodcastMapper {

  fun map(podcast: Podcast): EpisodeUiModel {
    return EpisodeUiModel(
        number = podcast.number,
        title = podcast.title,
        topics = podcast.timeLabels.joinToString(
            separator = "\n",
        ) {
          buildString {
            append("- ")
            append(it.topic)
          }
        },
        url = podcast.url,
    )
  }
}
