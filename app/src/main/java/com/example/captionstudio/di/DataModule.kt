package com.example.captionstudio.di

import com.example.captionstudio.data.LocalTranscriptionsRepo
import com.example.captionstudio.data.TranscriptionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun provideTranscriptionRepository(transcriptionRepository: LocalTranscriptionsRepo): TranscriptionRepository
}

