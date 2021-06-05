package com.stelmashchuk.radiot.presentation.themes.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stelmashchuk.radiot.data.ThemesRepository
import com.stelmashchuk.radiot.di.Graph

class ThemeViewModelFactory(val number: Long) : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return ThemeViewModel(number = number) as T
  }
}

data class ThemeUiModel(
    val title: String,
    val url: String,
)

class ThemeViewModel(
    private val themesRepository: ThemesRepository = Graph.themeRepository,
    private val number: Long,
) : ViewModel() {

  private val _data = MutableLiveData<ThemeUiModel>()
  val data: LiveData<ThemeUiModel> = _data

  init {
    themesRepository.getThemeFromCache(number)?.let { theme ->
      _data.postValue(ThemeUiModel(theme.title, theme.url))
    }
  }
}
