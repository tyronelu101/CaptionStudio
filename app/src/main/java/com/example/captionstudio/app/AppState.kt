package com.example.captionstudio.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.captionstudio.app.navigation.TopLevelDestinations
import com.example.captionstudio.app.navigation.TopLevelDestinations.*
import com.example.captionstudio.home.HomeScreenRoute
import com.example.captionstudio.transcriptions.TranscriptionsRoute


@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()): AppState {

    return remember(navController)
    { AppState(navController) }
}

class AppState(val navController: NavHostController) {

    val currentNavDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow.collectAsState(null)
            return currentEntry.value?.destination
        }

    val currentTopLevelDestination: TopLevelDestinations?
        @Composable get() = TopLevelDestinations.entries.firstOrNull {
            currentNavDestination?.hasRoute(route = it.route) == true }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestinations) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
        }

//        when (topLevelDestination) {
//            HOME -> navController.navigate(HomeScreenRoute, topLevelNavOptions)
//            TRANSCRIPTIONS -> navController.navigate(TranscriptionsRoute, topLevelNavOptions)
//        }
    }
}