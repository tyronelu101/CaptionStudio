package com.example.captionstudio.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.captionstudio.R
import com.example.captionstudio.Result
import com.example.captionstudio.app.ui.CaptionStudioIcons
import com.example.captionstudio.domain.models.Transcription
import kotlinx.serialization.Serializable
import java.util.Date
import kotlin.math.exp

@Serializable
data object HomeScreenRoute

@Composable
fun HomeScreen(
    onNavigateToStudio: (transcription: Transcription) -> Unit,
    onTranscriptionItemClick: (transcription: Transcription) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val saveResult by viewModel.saveResult.collectAsState()
    val transcriptions by viewModel.transcriptions.collectAsState()

    LaunchedEffect(saveResult) {
        saveResult?.let {
            if (it is Result.Success<Transcription>) {
                onNavigateToStudio(it.data)
            }
            viewModel.reset()
        }
    }

    HomeScreenContent(
        transcriptions,
        onTranscriptionItemClick,
        {},
        { id -> viewModel.deleteTranscription(id) },
        onConfirmDialog = {
            val transcription =
                Transcription(
                    name = it,
                    language = "",
                    audioURI = "${context.filesDir.path}/${System.currentTimeMillis()}.pcm",
                    date = Date(System.currentTimeMillis())
                )
            viewModel.insertNewTranscription(transcription)
        }, modifier
    )
}

@Composable
private fun HomeScreenContent(
    transcriptions: List<Transcription>,
    onTranscriptionItemClick: (transcription: Transcription) -> Unit,
    onExport: (transcriptionId: Long) -> Unit,
    onDelete: (transcription: Transcription) -> Unit,
    onConfirmDialog: (title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, "Floating action button")
            }
        }) { innerPadding ->
        if (showDialog) {
            AddTranscriptionDialog(
                onConfirm = {
                    showDialog = false
                    onConfirmDialog(it)
                },
                onDismiss = {
                    showDialog = false
                })
        }
        TranscriptionsList(
            transcriptions,
            onTranscriptionItemClick,
            onExport,
            onDelete,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun TranscriptionsList(
    transcriptions: List<Transcription>,
    onTranscriptionItemClick: (Transcription) -> Unit,
    onExport: (transcriptionId: Long) -> Unit,
    onDelete: (transcription: Transcription) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(transcriptions) { transcription ->
            TranscriptionItem(
                transcription,
                onTranscriptionItemClick,
                onExport = {},
                onDelete = onDelete,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun TranscriptionItem(
    transcription: Transcription,
    onItemClick: (transcription: Transcription) -> Unit,
    onExport: (transcriptionId: Long) -> Unit,
    onDelete: (transcription: Transcription) -> Unit,
    modifier: Modifier = Modifier
) {

    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .fillMaxWidth(1f)
            .padding(8.dp)
            .clickable(true, onClick = {
                onItemClick(transcription)
            })
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(1f)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = modifier.weight(0.9f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = transcription.name,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = transcription.date.toString(),
                    fontSize = 14.sp,
                    color = Color.Gray,
                )

            }
            Box(modifier = modifier.weight(0.1f)) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = CaptionStudioIcons.MENU,
                        contentDescription = stringResource(R.string.check)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Export") },
                        onClick = { expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDelete(transcription)
                        }
                    )
                }
            }

        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0XFF0FF00)
private fun TranscriptionItemPreview() {

    val transcription = Transcription(
        name = "This is a versy asdasd sad asds ad asdsadasd sadsad",
        audioURI = "",
        language = "",
        date = Date(System.currentTimeMillis())
    )
    TranscriptionItem(
        modifier = Modifier,
        transcription = transcription,
        onItemClick = {},
        onExport = {},
        onDelete = { }
    )

}

@Composable
fun HomeScreenPreview() {
//    HomeScreen(onAddTranscription = {}, modifier = Modifier)
}

