package com.example.captionstudio.domain.transcriber

import android.content.Context
import android.util.Log
import com.example.captionstudio.AudioSettings
import com.example.captionstudio.R
import com.example.captionstudio.domain.models.Word
import com.google.api.gax.rpc.ClientStream
import com.google.api.gax.rpc.ResponseObserver
import com.google.api.gax.rpc.StreamController
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.SpeechClient
import com.google.cloud.speech.v1.SpeechSettings
import com.google.cloud.speech.v1.StreamingRecognitionConfig
import com.google.cloud.speech.v1.StreamingRecognizeRequest
import com.google.cloud.speech.v1.StreamingRecognizeResponse
import com.google.protobuf.ByteString
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class GoogleTranscriber @Inject constructor(
    @ApplicationContext private val context: Context
) : Transcriber {

    override var resultFlow: MutableSharedFlow<TranscriberResult> =
        MutableSharedFlow(extraBufferCapacity = 10)

    //Used to create the client stream
    private var client: SpeechClient
    //Used to send the actual audio data. Attach observer

    private lateinit var clientStream: ClientStream<StreamingRecognizeRequest>

    //Sent by client stream. First request sent
    private val streamingRecognitionConfig: StreamingRecognitionConfig
    private var referenceToStreamController: StreamController? = null

    private var newSession: Boolean = true
    private val streamingLimit = 290_000 //~5 minutes

    private var currentSessionStartingTimeMs: Long = 0L
    private var resultEndTime: Long = 0L
    private var finalRequestEndTime: Long = 0L

    private lateinit var request: StreamingRecognizeRequest

    init {
        client = createSpeechClient()

        //Sent by
        val recognitionConfig = RecognitionConfig.newBuilder()
            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
            .setSampleRateHertz(AudioSettings.SAMPLE_RATE_HZ)
            .setLanguageCode("en")
            .setEnableWordConfidence(true)
            .setEnableWordTimeOffsets(true)
            .build()

        streamingRecognitionConfig =
            StreamingRecognitionConfig.newBuilder()
                .setConfig(recognitionConfig)
                .setInterimResults(false)
                .build()

    }

    override
    suspend fun transcribe(data: ByteArray) {
        try {
            val estimatedTime = System.currentTimeMillis() - currentSessionStartingTimeMs
            if (!newSession && estimatedTime >= streamingLimit) {

                clientStream.closeSend()
                referenceToStreamController?.cancel()

                if (resultEndTime > 0) {
                    finalRequestEndTime = resultEndTime
                }
                resultEndTime = 0
                newSession = true

            } else {
                if (newSession) {
                    currentSessionStartingTimeMs = System.currentTimeMillis()
                    newSession = false

                    clientStream =
                        client.streamingRecognizeCallable().splitCall(createResponseObserver())

                    request = StreamingRecognizeRequest.newBuilder()
                        .setStreamingConfig(streamingRecognitionConfig)
                        .build()
                    // The first request in a streaming call has to be a config
                    // After this initial request will be the audio data
                    clientStream.send(request)
                }

                request = StreamingRecognizeRequest.newBuilder()
                    .setAudioContent(
                        ByteString.copyFrom(data)
                    )
                    .build()
                clientStream.send(request)

            }
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Failed to recognize audio: ${e}")
        }
    }

    override fun stop() {
        clientStream.closeSend()
        referenceToStreamController?.cancel()
        newSession = true
    }

    private fun createSpeechClient(): SpeechClient {
        val keyStream = context.resources.openRawResource(R.raw.speech_key)
        val credentials = GoogleCredentials.fromStream(keyStream)
            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

        val speechSettings = SpeechSettings.newBuilder()
            .setCredentialsProvider { credentials }
            .setTransportChannelProvider(
                SpeechSettings.defaultGrpcTransportProviderBuilder()
                    .setEndpoint("speech.googleapis.com:443") // << force endpoint
                    .build()
            )
            .build()

        return SpeechClient.create(speechSettings)
    }

    private fun createResponseObserver() = object : ResponseObserver<StreamingRecognizeResponse> {

        override fun onStart(controller: StreamController?) {
            referenceToStreamController = controller
            Log.i("GoogleTranscriber", "On Start")

        }

        override fun onResponse(response: StreamingRecognizeResponse) {

            Log.i("GoogleTranscriber", "On response ${response}")
            if (response.resultsList.isNotEmpty()) {
                val result = response.resultsList[0]
                if (result.isFinal) {
                    val result = response.resultsList[0]
                    val emitResult = resultFlow.tryEmit(
                        TranscriberResult.FinalResult(
                            result.alternativesList[0].wordsList.map {
                                val wordStartTime =
                                    (it.startTime.seconds * 1000) + (it.startTime.nanos / 1000000)
                                val wordEndTime =
                                    (it.endTime.seconds * 1000) + (it.endTime.nanos / 1000000)
                                Word(
                                    0L,
                                    captionId = 0L,
                                    text = it.word,
                                    confidence = it.confidence.toInt(),
                                    startTime = wordStartTime,
                                    endTime = wordEndTime
                                )
                            },
                        )
                    )

                    Log.i("GoogleTranscriber", "Trying to emit result was ${emitResult}")


                } else {
                    resultFlow.tryEmit(
                        TranscriberResult.PartialResult(
                            result.alternativesList[0].transcript
                        )
                    )
                }
            }
        }

        override fun onError(t: Throwable?) {
            Log.i("GoogleTranscriber", "on error ${t}")
        }

        override fun onComplete() {
            Log.i("GoogleTranscriber", "on complete")
        }
    }
}