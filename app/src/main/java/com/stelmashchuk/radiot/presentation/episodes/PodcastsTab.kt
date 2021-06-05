package com.stelmashchuk.radiot.presentation.episodes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.stelmashchuk.radiot.presentation.details.DetailView
import androidx.navigation.compose.navArgument

private object Destinations {
  const val LIST = "list"

  const val KEY_PODCAST_NUMBER = "podcastNumber"
  const val DETAILS = "details"
}

private class Actions(navController: NavHostController) {
  val openDetails: (Long) -> Unit = {
    navController.navigate("${Destinations.DETAILS}/$it")
  }
}

@Composable
fun PodcastsTab() {
  val navController = rememberNavController()
  val actions = remember(navController) {
    Actions(navController)
  }
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
