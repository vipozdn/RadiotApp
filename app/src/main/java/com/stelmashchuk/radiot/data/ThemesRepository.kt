package com.stelmashchuk.radiot.data

class ThemesRepository(
    private val radiotService: RadiotService,
) {

  private val cache = mutableMapOf<String, Theme>()

  suspend fun loadTheme(): List<Theme> {
    return radiotService.getThemes(PREP_COUNT).also { themes ->
      cache.putAll(themes.map { Pair(it.url, it) })
    }
  }

  fun getThemeFromCache(number: String): Theme? {
    return cache[number]
  }

  companion object {
    const val PREP_COUNT = 50L
  }

}