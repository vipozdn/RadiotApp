package com.stelmashchuk.radiot.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stelmashchuk.radiot.data.PodcastRepository
import com.stelmashchuk.radiot.di.Graph
import com.stelmashchuk.radiot.presentation.episodes.EpisodeUiModel
import com.stelmashchuk.radiot.presentation.episodes.PodcastMapper

class DetailsViewModelFactory(private val podcastNumber: Long) : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return DetailsViewModel(podcastNumber = podcastNumber) as T
  }
}

class DetailsViewModel(
    podcastNumber: Long,
    podcastRepository: PodcastRepository = Graph.podcastRepository,
    private val podcastMapper: PodcastMapper = PodcastMapper(),
) : ViewModel() {

  private val _podcast = MutableLiveData<EpisodeUiModel>()
  val podcast: LiveData<EpisodeUiModel> = _podcast

  init {
    podcastRepository.getPodcastFromCache(podcastNumber)?.let { podcast ->
      _podcast.postValue(podcastMapper.map(podcast))
    }
  }
}
