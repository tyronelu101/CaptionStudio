package com.example.captionstudio.app

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.captionstudio.app.navigation.CaptionStudioNavHost
import com.example.captionstudio.app.navigation.TopLevelDestinations
import com.example.captionstudio.home.AddTranscriptionDialog

@Composable
fun App(appState: AppState, modifier: Modifier = Modifier) {

    val currentDestination = appState.currentNavDestination
    val showNavigationBar =
        TopLevelDestinations.entries.contains(appState.currentTopLevelDestination)
    
    NavigationSuiteScaffold(
        layoutType = if (showNavigationBar) NavigationSuiteType.NavigationBar else NavigationSuiteType.None,
        navigationSuiteItems = {
            if (showNavigationBar) {
                TopLevelDestinations.entries.forEach { topLevelDestination ->
                    val isSelected =
                        currentDestination?.hierarchy?.any { it.hasRoute(topLevelDestination.route) }
                            ?: false
                    item(
                        icon = {
                            Icon(
                                topLevelDestination.icon,
                                contentDescription = stringResource(id = topLevelDestination.contentDescription)
                            )
                        },
                        label = { Text(text = stringResource(id = topLevelDestination.label)) },
                        selected = isSelected,
                        onClick = { appState.navigateToTopLevelDestination(topLevelDestination) }
                    )
                }
            }
        }
    ) {
        CaptionStudioNavHost(navController = appState.navController)
    }
}

