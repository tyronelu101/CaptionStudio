package com.example.captionstudio.data

import android.util.Log
import com.example.captionstudio.database.CaptionsDao
import com.example.captionstudio.database.TranscriptionDao
import com.example.captionstudio.database.WordDao
import com.example.captionstudio.database.entities.CaptionEntity
import com.example.captionstudio.domain.models.Caption
import com.example.captionstudio.domain.models.Transcription
import com.example.captionstudio.domain.models.toEntity
import com.example.captionstudio.domain.models.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalTranscriptionsRepo @Inject constructor(
    private val transcriptionsDao: TranscriptionDao,
    private val captionsDao: CaptionsDao,
    private val wordDao: WordDao
) :
    TranscriptionRepository {
    override suspend fun insertTranscription(transcription: Transcription): Long {
        return transcriptionsDao.insert(transcription.toEntity())
    }

    override suspend fun getTranscription(id: Long): Transcription {
        return transcriptionsDao.get(id).toModel()
    }

    override suspend fun delete(transcription: Transcription) {
        transcriptionsDao.delete(transcription.toEntity())
    }

    override suspend fun insertCaption(
        transcriptionId: Long,
        caption: Caption
    ) {
        val captionEntity = CaptionEntity(id = caption.id, transcriptionId)
        val captionId = captionsDao.upsert(captionEntity)

        wordDao.upsertAll(caption.words.map {
            it.copy(captionId = captionId).toEntity()
        })
    }

    override fun getTranscriptions(): Flow<List<Transcription>> =
        transcriptionsDao.getTranscriptions().map { transcriptions ->
            transcriptions.map { transcription ->
                transcription.toModel()
            }
        }

    override fun getCaptionsForTranscription(transcriptionId: Long): Flow<List<Caption>> {
        val captions =
            captionsDao.getCaptionsWithWordsByTranscriptionId(transcriptionId).map { entityList ->
                entityList.map { entity ->
                    entity.toModel()
                }
            }
        return captions
    }
}