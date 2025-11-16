package com.example.captionstudio.audiosource

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.serialization.Serializable

@Serializable
data object StudioModeRoute

@Composable
fun StudioModeScreen(
    modifier: Modifier = Modifier,
    viewModel: AudioSourceViewModel = hiltViewModel()
) {
    StudioModeScreen( modifier)
}

@Composable
private fun StudioModeScreen(
    modifier: Modifier
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
    }

}

@Composable
fun HomeScreenPreview() {
    StudioModeScreen( modifier = Modifier)
}