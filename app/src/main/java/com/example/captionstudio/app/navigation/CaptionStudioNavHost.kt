package com.example.captionstudio.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.captionstudio.home.HomeScreen
import com.example.captionstudio.home.HomeScreenRoute
import com.example.captionstudio.studio.StudioRoute
import com.example.captionstudio.studio.StudioScreen

@Composable
fun CaptionStudioNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = HomeScreenRoute) {
        composable<HomeScreenRoute> {
            HomeScreen(
                onTranscriptionItemClick = { transcription ->
                    navController.navigate(
                        StudioRoute(
                            transcriptionId = transcription.id,
                            audioPath = transcription.audioURI
                        )
                    )
                },
                onNavigateToStudio = { transcription ->
                    navController.navigate(
                        StudioRoute(
                            transcriptionId = transcription.id,
                            audioPath = transcription.audioURI
                        )
                    )
                })
        }
        composable<StudioRoute> { StudioScreen() }
    }
}