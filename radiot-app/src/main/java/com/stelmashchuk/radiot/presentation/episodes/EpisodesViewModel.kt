package com.stelmashchuk.radiot.presentation.episodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.radiot.data.PodcastRepository
import com.stelmashchuk.radiot.di.Graph
import kotlinx.coroutines.launch

class EpisodesViewModel(
    private val podcastRepository: PodcastRepository = Graph.podcastRepository,
    private val podcastMapper: PodcastMapper = PodcastMapper(),
) : ViewModel() {

  private val _posts = MutableLiveData<List<EpisodeUiModel>>()
  val post: LiveData<List<EpisodeUiModel>> = _posts

  init {
    viewModelScope.launch {
      _posts.postValue(podcastRepository.loadPodcasts().map(podcastMapper::map))
    }
  }
}

data class EpisodeUiModel(
    val number: Long,
    val title: String,
    val topics: String,
    val url: String,
)

