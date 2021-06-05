package com.stelmashchuk.radiot.presentation.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.stelmashchuk.radiot.presentation.themes.details.ThemeDetails
import com.stelmashchuk.radiot.presentation.themes.list.ThemeList

private object Destinations {
  const val LIST = "list"

  const val KEY_THEME_NUMBER = "podcastNumber"
  const val DETAILS = "details"
}

private class Actions(navController: NavHostController) {
  val openDetails: (Long) -> Unit = {
    navController.navigate("${Destinations.DETAILS}/$it")
  }
}

@Composable
fun ThemesTab() {
  val navController = rememberNavController()
  val actions = remember(navController) {
    Actions(navController)
  }
  NavHost(navController = navController, startDestination = Destinations.LIST) {
    composable(Destinations.LIST) {
      ThemeList(actions.openDetails)
    }
    composable(Destinations.DETAILS + "/{${Destinations.KEY_THEME_NUMBER}}",
        arguments = listOf(
            navArgument(Destinations.KEY_THEME_NUMBER) {
              type = NavType.LongType
            })
    ) {
      ThemeDetails(it.arguments?.getLong(Destinations.KEY_THEME_NUMBER))
    }
  }
}
