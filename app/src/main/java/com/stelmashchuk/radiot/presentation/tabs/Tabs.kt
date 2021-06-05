package com.stelmashchuk.radiot.presentation.tabs

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.stelmashchuk.radiot.R

enum class Tabs(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {
  PODCASTS(R.string.label_podcasts, R.drawable.ic_podcasts),
  PRE_SHOW(R.string.label_pre_show, R.drawable.ic_preshow),
}
