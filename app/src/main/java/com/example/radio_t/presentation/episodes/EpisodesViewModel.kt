package com.example.radio_t.presentation.episodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radio_t.di.Graph
import com.example.radio_t.data.Podcast
import com.example.radio_t.data.PodcastRepository
import com.example.radio_t.data.RadiotService
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
