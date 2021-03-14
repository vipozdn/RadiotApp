package com.example.radio_t.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.radio_t.presentation.details.DetailView
import com.example.radio_t.presentation.episodes.EpisodesView
import com.example.radio_t.ui.RadiotTheme

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