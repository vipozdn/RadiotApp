package com.stelmashchuk.radiot.presentation.themes.details

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ThemeDetails(number: String?) {
  if (number == null) {
    Text(text = "Error")
  } else {

  }
  Text(text = "prep details $number")
}

@Composable
fun ThemeContent() {

}