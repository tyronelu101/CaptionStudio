package com.example.captionstudio.di

import com.example.captionstudio.domain.player.AndroidAudioPlayer
import com.example.captionstudio.domain.player.AudioPlayerFactory
import com.example.captionstudio.domain.recorder.AndroidAudioRecorder
import com.example.captionstudio.domain.recorder.AudioRecorderFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class AudioModule {
    @Binds
    abstract fun bindAudioRecorderFactory(factory: AndroidAudioRecorder.Factory): AudioRecorderFactory

    @Binds
    abstract fun bindAudioPlayerFactory(factory: AndroidAudioPlayer.Factory): AudioPlayerFactory
}