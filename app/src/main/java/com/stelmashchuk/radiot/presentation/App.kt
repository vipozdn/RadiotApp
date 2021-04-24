package com.stelmashchuk.radiot.presentation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.stelmashchuk.radiot.presentation.details.DetailView
import com.stelmashchuk.radiot.presentation.episodes.EpisodesView
import com.stelmashchuk.radiot.presentation.tabs.StartScreen
import com.stelmashchuk.radiot.ui.RadiotTheme

@Composable
fun App() {
  RadiotTheme {
    StartScreen()
  }
}
