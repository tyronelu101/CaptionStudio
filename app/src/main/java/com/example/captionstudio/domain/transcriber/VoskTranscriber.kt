//package com.example.captionstudio.domain.transcriber
//
//import android.content.Context
//import android.util.Log
//import com.example.captionstudio.domain.models.Caption
//import com.example.captionstudio.domain.models.Word
//import com.example.captionstudio.domain.transcriber.TranscriberState.Idle
//import com.example.captionstudio.domain.transcriber.TranscriberState.Result
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.coroutines.flow.MutableStateFlow
//import org.json.JSONObject
//import org.vosk.Model
//import org.vosk.Recognizer
//import org.vosk.android.SpeechService
//import org.vosk.android.StorageService
//import java.io.FileInputStream
//import javax.inject.Inject
//
//
//class VoskTranscriber @Inject constructor(@ApplicationContext private val context: Context) :
//    Transcriber {
//
//    override val result: MutableStateFlow<TranscriberState> =
//        MutableStateFlow(Idle)
//
//    private lateinit var model: Model
//    private lateinit var recognizer: Recognizer
//    private lateinit var speechService: SpeechService
//    private val voskResultParser = VoskResultParser()
//
//    init {
//        StorageService.unpack(context, "vosk-model-small-en-us-0.15", "model", {
//            this.model = it
//            recognizer = Recognizer(model, 16000.0f)
//            recognizer.setWords(true)
//
//        }, {
//            Log.e(
//                VoskTranscriber::class.simpleName, "Failed to unpack model ${it.message}"
//            )
//        })
//    }
//
//    override suspend fun start(data: ByteArray) {
//        val isFinal = recognizer.acceptWaveForm(data, data.size)
//        if (isFinal) {
//            val words = voskResultParser.parse(recognizer.finalResult).map {
//                Word(
//                    timeStart = it.start,
//                    timeEnd = it.end,
//                    text = it.word,
//                    confidence = it.conf
//                )
//            }
//            result.emit(
//                Result(Caption(words))
//            )
//            result.emit(
//                Idle
//            )
//        } else {
//            val caption = JSONObject(recognizer.partialResult).getString("partial") ?: ""
//            result.emit(TranscriberState.PartialResult(caption))
//        }
//    }
//
//    override fun stop() {
//        recognizer.close()
//        speechService.stop()
//    }
//}