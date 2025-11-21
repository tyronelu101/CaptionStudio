package com.example.captionstudio.di

import com.example.captionstudio.domain.export.CaptionExporter
import com.example.captionstudio.domain.export.LocalCaptionExporter
import com.example.captionstudio.domain.transcriber.GoogleTranscriber
import com.example.captionstudio.domain.transcriber.Transcriber
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindTranscriber(transcriber: GoogleTranscriber): Transcriber

    @Binds
    abstract fun bindExporter(exporter: LocalCaptionExporter): CaptionExporter
}