package com.stelmashchuk.radiot.presentation.episodes

import com.stelmashchuk.radiot.data.Podcast

class PodcastMapper {

  fun map(podcast: Podcast): EpisodeUiModel {
    return EpisodeUiModel(
        number = podcast.number,
        title = podcast.title,
        topics = podcast.timeLabels?.joinToString(
            separator = "\n",
        ) {
          buildString {
            append("- ")
            append(it.topic)
          }
        } ?: "",
        url = podcast.url,
    )
  }
}
