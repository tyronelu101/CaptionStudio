package com.example.captionstudio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.captionstudio.R
import com.example.captionstudio.app.ui.CaptionStudioIcons

@Composable
fun CaptionItem(
    text: String,
    startTime: String,
    endTime: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {

    var isEditing by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .fillMaxWidth(1f)
            .padding(4.dp),
        shape = MaterialTheme.shapes.small,
        shadowElevation = 3.dp
    ) {
        Column(modifier = modifier.padding(8.dp)) {
            Column(modifier = modifier.padding(8.dp)) {
                if (isEditing) {
                    TextField(
                        modifier = modifier
                            .background(Color.Transparent)
                            .fillMaxWidth(1f),
                        state = rememberTextFieldState(initialText = text),
                        colors = TextFieldDefaults.colors(
//                            unfocusedContainerColor = Color.Transparent,
//                            focusedIndicatorColor = Color.Transparent,
//                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                } else {
                    Text(text)
                }

                if (!isEditing) {
                    HorizontalDivider(modifier = modifier.padding(top = 24.dp, bottom = 16.dp))
                }
                Row(
                    modifier = modifier.fillMaxWidth(1f).padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    if (isEditing) {
                        IconButton(
                            onClick = {
                                onEdit()
                                isEditing = false
                            },
                            modifier = Modifier.size(24.dp)

                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = CaptionStudioIcons.CHECK,
                                contentDescription = stringResource(R.string.check)
                            )
                        }
                        IconButton(
                            onClick = {
                                isEditing = false
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = CaptionStudioIcons.CLOSE,
                                contentDescription = stringResource(R.string.close)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                isEditing = !isEditing
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = CaptionStudioIcons.EDIT,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                        IconButton(
                            onClick = {
                            }, modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = CaptionStudioIcons.CUT,
                                contentDescription = stringResource(R.string.cut)
                            )
                        }
                    }
                }

            }
            Row(
                modifier = modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = modifier
                        .alpha(0.60f),
                    text = startTime,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
                Text(
                    modifier = modifier
                        .alpha(0.60f),
                    text = endTime,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun CardItemPreview() {
    CaptionItem(
        text = "This is a very long text. Helloasdsadsad sads adsa dasd sa dsad sa dsa.",
        startTime = "00:00:00.000",
        endTime = "00:05:01.550",
        onEdit = {},
        modifier = Modifier
    )
}