package com.stelmashchuk.remark.feature.root

import androidx.compose.material.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stelmashchuk.remark.ResourcesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RemarkViewModel(private val resourcesRepository: ResourcesRepository) : ViewModel() {

  val snackBar: SnackbarHostState = SnackbarHostState()

  init {
    viewModelScope.launch {
      SnackBarBus.snackBar
          .collect { state ->
            if (state != null && state is SnackBarBus.SnackBarData.ById) {
              snackBar.showSnackbar(resourcesRepository.getString(state.msgId))
            }
          }
    }
  }
}
