package com.example.captionstudio.domain.export

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.example.captionstudio.data.TranscriptionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import androidx.core.net.toUri
import com.example.captionstudio.util.TimeUtil

/*
Format of an SRT file
    - A sequence number for each frame
    - Beginning and ending time code
    - A caption for each time code
    - A blank line after each frame
    e.g.
    1
    00:00:00,000 ---> 00:00:05,000
    This is the first subtitle

    2
    00:00:08,000 ---> 00:00:11,000
    This is the second subtitle
 */
class LocalCaptionExporter @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val captionRepo: TranscriptionRepository
) :
    CaptionExporter {
    override suspend fun export(transcriptionId: Long, uri: String) {
        val uri = uri.toUri()
        val dirDocId = DocumentsContract.getTreeDocumentId(uri)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri, dirDocId)

        withContext(Dispatchers.IO) {
            val transcription = captionRepo.getTranscription(transcriptionId)
            val fileName = transcription.name + ".srt"
            val fileUri = DocumentsContract.createDocument(
                appContext.contentResolver,
                childrenUri,
                "application/octet-stream",
                fileName
            )

            fileUri?.let { fileUri ->
                captionRepo.getCaptionsForTranscription(transcriptionId).collect { captions ->
                    appContext.contentResolver.openOutputStream(fileUri)?.use { out ->
                        captions.forEachIndexed { index, caption ->
                            val content = """
                                ${index + 1}
                                ${TimeUtil.convertMillisecondsToSrtTime(caption.startingTime)} ---> ${
                                TimeUtil.convertMillisecondsToSrtTime(
                                    caption.endingTime
                                )
                            }
                                ${caption.words.joinToString(" ") { it.text }}
                                
                                
                            """.trimIndent()
                            //The spaces above are on purpose. Leave there.

                            out.write(content.toByteArray())
                        }
                    }
                }
            }
        }
    }
}