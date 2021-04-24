package com.stelmashchuk.radiot.presentation.themes.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.radiot.data.ThemesRepository
import com.stelmashchuk.radiot.di.Graph
import kotlinx.coroutines.launch

class ThemeListViewModel(
    private val radiotService: ThemesRepository = Graph.themeRepository,
) : ViewModel() {

  private val _themes = MutableLiveData<List<ThemeUiModel>>()
  val themes: LiveData<List<ThemeUiModel>> = _themes

  init {
    viewModelScope.launch {
      _themes.postValue(radiotService.loadTheme()
          .map {
            ThemeUiModel(it.title, it.url)
          }
      )
    }
  }
}

data class ThemeUiModel(
    val title: String,
    val url: String,
)
