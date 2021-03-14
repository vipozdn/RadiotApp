package com.example.radio_t.presentation.episodes

import com.example.radio_t.data.Podcast

class PodcastMapper {

  fun map(podcast: Podcast): EpisodeUiModel {
    return EpisodeUiModel(
        podcast.number,
        podcast.title,
        podcast.timeLabels.joinToString(
            separator = "\n",
        ) {
          buildString {
            append("- ")
            append(it.topic)
          }
        }
    )
  }

}
