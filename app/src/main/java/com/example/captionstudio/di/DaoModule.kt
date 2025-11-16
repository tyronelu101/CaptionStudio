package com.example.captionstudio.di

import com.example.captionstudio.database.CaptionStudioDatabase
import com.example.captionstudio.database.CaptionsDao
import com.example.captionstudio.database.TranscriptionDao
import com.example.captionstudio.database.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    fun provideTranscriptionsDao(
        database: CaptionStudioDatabase
    ): TranscriptionDao = database.transcriptionDao()

    @Provides
    fun provideCaptionsDao(
        database: CaptionStudioDatabase
    ): CaptionsDao = database.captionsDao()

    @Provides
    fun provideWordDao(
        database: CaptionStudioDatabase
    ): WordDao = database.wordDao()
}