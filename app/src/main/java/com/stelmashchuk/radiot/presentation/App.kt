package com.stelmashchuk.radiot.presentation

import androidx.compose.runtime.Composable
import com.stelmashchuk.radiot.presentation.tabs.StartScreen
import com.stelmashchuk.radiot.ui.RadiotTheme

@Composable
fun App() {
  RadiotTheme {
    StartScreen()
  }
}
