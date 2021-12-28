package com.stelmashchuk.remark.feature.root

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal object SnackBarBus {

  sealed class SnackBarData {
    data class ById(@StringRes val msgId: Int) : SnackBarData()
  }

  private val _snackBar = MutableStateFlow<SnackBarData?>(null)
  val snackBar: StateFlow<SnackBarData?> = _snackBar

  suspend fun showSnackBar(@StringRes msgId: Int) {
    _snackBar.emit(SnackBarData.ById(msgId))
  }
}
