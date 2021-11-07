package com.stelmashchuk.radiot.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.stelmashchuk.radiot.presentation.tabs.StartScreen
import com.stelmashchuk.radiot.ui.RadiotTheme

@Composable
fun App() {
  RadiotTheme {
    StartScreen()
  }
}

@Preview
@Composable
fun AppPreview() {
  App()
}
