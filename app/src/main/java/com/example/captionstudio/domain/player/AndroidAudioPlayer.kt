package com.example.captionstudio.domain.player

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.AudioTrack.MODE_STREAM
import android.util.Log
import com.example.captionstudio.AudioSettings
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream


class AndroidAudioPlayer @AssistedInject constructor(
    @Assisted private val audioPath: String
) : AudioPlayer {

    private val pollingRate = 100L

    private var audioPlayer: AudioTrack? = null
    private var listener: AudioPlayerListener? = null

    private val buffer = AudioTrack.getMinBufferSize(
        AudioSettings.SAMPLE_RATE_HZ,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var pcmData: ByteArray = byteArrayOf()

    private var position: Int = 0

    private val sampleRateHz = AudioSettings.SAMPLE_RATE_HZ

    //sample rate * channel(mono-1, stereo-2), * (8 bit encoding-1, 16bit-2)
    private val bytesPerSecond = sampleRateHz * 1 * 2
    private val audioFile = File(audioPath)

    private var isPlaying = false

    init {
        val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val audioFormat =
            AudioFormat.Builder().setSampleRate(AudioSettings.SAMPLE_RATE_HZ)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT).build()
        audioPlayer = AudioTrack(
            audioAttributes, audioFormat,
            buffer, MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE
        )
    }

    override fun play(audioPath: String) {
        if (audioFile.exists() && audioFile.length() > 0) {
            val fileInputStream = FileInputStream(audioFile)
            pcmData = ByteArray(fileInputStream.available())
            fileInputStream.read(pcmData)
            fileInputStream.close()

            audioPlayer?.play()
            var chunkSize: Int = buffer
            isPlaying = true
            CoroutineScope(Dispatchers.IO).launch {
                while (position < pcmData.size) {
                    chunkSize = chunkSize.coerceAtMost(pcmData.size - position)
                    audioPlayer?.write(pcmData, position, chunkSize)
                    position += chunkSize
                }
                isPlaying = false
                listener?.onFinish()
            }

            if (listener != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    while (isPlaying) {
                        listener?.onProgress(getCurrentTimeStamp())
                        delay(pollingRate)
                    }
                }
            }
        }
    }

    override fun pause() {
        audioPlayer?.pause()
        isPlaying = false
    }

    override fun stop() {
        audioPlayer?.stop()
        audioPlayer?.release()
        audioPlayer = null
        listener = null
    }

    override fun seek(time: Int) {
        position = time * bytesPerSecond
        listener?.onProgress(getCurrentTimeStamp())
    }

    private fun getCurrentTimeStamp(): Int = position / bytesPerSecond

    override fun setOnProgressListener(listener: AudioPlayerListener) {
        this.listener = listener
    }

    @AssistedFactory
    interface Factory : AudioPlayerFactory {
        override fun create(audioPath: String): AndroidAudioPlayer
    }
}