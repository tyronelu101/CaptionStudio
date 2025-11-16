package com.example.captionstudio.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.captionstudio.R
import com.example.captionstudio.app.ui.CaptionStudioIcons
import kotlinx.serialization.Serializable

@Serializable
data object AddTranscriptionDialog

@Composable
fun AddTranscriptionDialog(
    onConfirm: (transcriptionName: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text = rememberTextFieldState("")

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = modifier
        ) {
            Column(modifier = modifier) {
                TextField(
                    modifier = modifier.padding(16.dp),
                    state = text,
                    placeholder = { Text("Title") }
                )
                Row(
                    modifier = modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {
                        onConfirm(text.text.toString())
                        text.setTextAndPlaceCursorAtEnd("")
                    }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = CaptionStudioIcons.CHECK,
                            contentDescription = stringResource(R.string.check)
                        )
                    }
                    IconButton(onClick = {
                        text.setTextAndPlaceCursorAtEnd("")
                        onDismiss()
                    }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = CaptionStudioIcons.CLOSE,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    AddTranscriptionDialog(
        onConfirm = {},
        onDismiss = {},
        modifier = Modifier
    )
}