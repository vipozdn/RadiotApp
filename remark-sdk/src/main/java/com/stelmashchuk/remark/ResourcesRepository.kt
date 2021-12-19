package com.stelmashchuk.remark

import android.content.Context
import androidx.annotation.StringRes

internal class ResourcesRepository(private val context: Context) {

  fun getString(@StringRes id: Int): String {
    return context.getString(id)
  }

}