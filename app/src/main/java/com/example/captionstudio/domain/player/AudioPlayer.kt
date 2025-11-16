package com.example.captionstudio.domain.player

interface AudioPlayer {
    fun play(audioPath: String)
    fun pause()
    fun stop()
    fun seek(time: Int)

    fun setOnProgressListener(listener: AudioPlayerListener)
}

interface AudioPlayerListener {
    fun onProgress(time: Int)
    fun onFinish()
}