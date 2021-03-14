package com.example.radio_t.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.radio_t.data.PodcastRepository
import com.example.radio_t.di.Graph
import com.example.radio_t.presentation.episodes.EpisodeUiModel
import com.example.radio_t.presentation.episodes.PodcastMapper

class DetailsViewModel(
    private val podcastRepository: PodcastRepository = Graph.podcastRepository,
    private val podcastMapper: PodcastMapper = PodcastMapper(),
) : ViewModel() {

  private val _podcast = MutableLiveData<EpisodeUiModel>()
  val podcast: LiveData<EpisodeUiModel> = _podcast

  fun loadPodcast(podcastNumber: Long) {
    val podcast = podcastRepository.getPodcastFromCache(podcastNumber) ?: return
    _podcast.postValue(podcastMapper.map(podcast))
  }
}
