package com.stelmashchuk.radiot.presentation.themes.details

import androidx.lifecycle.ViewModel
import com.stelmashchuk.radiot.data.ThemesRepository
import com.stelmashchuk.radiot.di.Graph

class ThemeViewModel(
    private val themesRepository: ThemesRepository = Graph.themeRepository,
) : ViewModel() {

  init {
    //themesRepository.getThemeFromCache()
  }

}