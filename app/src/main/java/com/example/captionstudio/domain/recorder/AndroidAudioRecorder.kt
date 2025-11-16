package com.example.captionstudio.domain.recorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTimestamp
import android.media.MediaRecorder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.captionstudio.AudioSettings
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

data class Amplitude(val amplitude: Float, val time: Float)

class AndroidAudioRecorder @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted private val audioPath: String
) :
    AudioRecorder {
    private val sampleRateHz = AudioSettings.SAMPLE_RATE_HZ
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    private val channels = AudioFormat.CHANNEL_IN_MONO

    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRateHz,
        channels,
        audioEncoding
    )

    private var isRecording = false
    private var recorder: AudioRecord? = null

    //sample rate * channel(mono-1, stereo-2), * (8 bit encoding-1, 16bit-2)
    private val bytesPerSecond = sampleRateHz * 1 * 2

    //A chunk is 1/4 of a second of data
    private val chunk = bytesPerSecond / 4
    private val segmentPerChunk = chunk / 3

    private val tick = 0.0833f

    private val audioFile: File = File(audioPath)

    init {
        if (audioFile.exists().not()) {
            audioFile.createNewFile()
        }
    }

    override fun start(
        path: String
    ): Flow<List<Byte>> = flow {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
//            Error. Permission was not granted
        }
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRateHz,
            channels,
            audioEncoding,
            bufferSize
        )

        recorder?.startRecording()
        isRecording = true

        //Byte array is signed [-32768, 32767]
        val audioBuffer = ByteArray(bufferSize)
        val outputFile = FileOutputStream(audioFile, true)

        val tempBuffer = mutableListOf<Byte>()

        while (isRecording) {
            val data = recorder?.read(audioBuffer, 0, bufferSize) ?: 0
            val pcmData = audioBuffer.take(data)
            emit(pcmData)
            tempBuffer.addAll(pcmData)
            //Each element in buffer is 1 byte. Audio is encoded in 2bytes.
            if (tempBuffer.size >= segmentPerChunk) {
                tempBuffer.subList(0, tempBuffer.size.coerceAtMost(segmentPerChunk)).clear()
//                amplitudeListener(Amplitude(processedData, tick * count))
            }

            if (data > 0) {
                outputFile.write(audioBuffer)
            }
        }
        outputFile.flush()
        outputFile.close()
    }.flowOn(Dispatchers.IO)

    //List of byte represents one sound bar
    //16bit and little endian
    private fun process(data: List<Byte>): Float {
        var sum = 0f
        val sampleCount = data.size / 2

        for (i in data.indices step 2) {
            //Need to combine data[i and i+1] to get 16bits
            //Converts the signed byte to an unsigned integer
            val leastSigBytes = (data[i].toInt() and 0XFF)
            val mostSigBytes = data[i + 1].toInt() shl 8
            val sample = (mostSigBytes or leastSigBytes).toShort().toInt()
            sum += sample.toDouble().pow(2.0).toFloat()
        }
        val rms = sqrt(sum / sampleCount)
        return rms / 32768
    }

    override fun getDuration(): Long {
        if (audioFile.exists()) {
            val size = audioFile.length()
            val duration = size / bytesPerSecond
            return duration
        }
        return 0L
    }

    override fun pause() {
        isRecording = false
    }

    override fun stop() {
        isRecording = false
        recorder?.stop()
        recorder?.release()
    }

    @AssistedFactory
    interface Factory : AudioRecorderFactory {
        override fun create(audioPath: String): AndroidAudioRecorder
    }
}