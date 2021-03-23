package com.stelmashchuk.radiot.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stelmashchuk.radiot.data.PodcastRepository
import com.stelmashchuk.radiot.di.Graph
import com.stelmashchuk.radiot.presentation.episodes.EpisodeUiModel
import com.stelmashchuk.radiot.presentation.episodes.PodcastMapper

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
