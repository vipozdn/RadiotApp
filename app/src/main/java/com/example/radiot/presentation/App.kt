package com.example.radiot.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.example.radiot.presentation.details.DetailView
import com.example.radiot.presentation.episodes.EpisodesView
import com.example.radiot.ui.RadiotTheme

object Destinations {
  const val LIST = "list"

  const val KEY_PODCAST_NUMBER = "podcastNumber"
  const val DETAILS = "details"
}

class Actions(navController: NavHostController) {
  val openDetails: (Long) -> Unit = {
    navController.navigate("${Destinations.DETAILS}/$it")
  }
}

@Composable
fun App() {
  val navController = rememberNavController()
  val actions = remember(navController) {
    Actions(navController)
  }
  RadiotTheme {
    NavHost(navController = navController, startDestination = Destinations.LIST) {
      composable(Destinations.LIST) {
        EpisodesView(actions.openDetails)
      }
      composable(Destinations.DETAILS + "/{${Destinations.KEY_PODCAST_NUMBER}}",
          arguments = listOf(
              navArgument(Destinations.KEY_PODCAST_NUMBER) {
                type = NavType.LongType
              })
      ) {
        DetailView(it.arguments?.getLong(Destinations.KEY_PODCAST_NUMBER))
      }
    }
  }
}
