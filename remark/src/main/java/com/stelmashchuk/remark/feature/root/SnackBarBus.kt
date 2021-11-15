package com.stelmashchuk.remark.feature.root

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object SnackBarBus {

  sealed class SnackBarData {
    data class ById(@StringRes val msgId: Int) : SnackBarData()
  }

  private val _snackBar = MutableSharedFlow<SnackBarData?>(replay = 1)
  val snackBar: SharedFlow<SnackBarData?> = _snackBar

  suspend fun showSnackBar(@StringRes msgId: Int) {
    _snackBar.emit(SnackBarData.ById(msgId))
  }

}