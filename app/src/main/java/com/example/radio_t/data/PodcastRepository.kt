package com.example.radio_t.data

class PodcastRepository(
    private val radiotService: RadiotService,
) {

  private val cache = mutableMapOf<Long, Podcast>()

  suspend fun loadPodcasts(): List<Podcast> {
    return radiotService.getPosts(posts = PODCAST_COUNT).also { podcasts ->
      cache.putAll(podcasts.map { Pair(it.number, it) })
    }
  }

  fun getPodcastFromCache(podcastNumber: Long): Podcast? {
    return cache[podcastNumber]
  }

  companion object {
    const val PODCAST_COUNT = 50L
  }
}