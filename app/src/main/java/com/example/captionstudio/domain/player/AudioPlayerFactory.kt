package com.example.captionstudio.domain.player

interface AudioPlayerFactory {
    fun create(audioPath: String): AudioPlayer
}