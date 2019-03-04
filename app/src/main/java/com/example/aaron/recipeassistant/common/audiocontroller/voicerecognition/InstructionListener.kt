package com.example.aaron.recipeassistant.common.audiocontroller.voicerecognition

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.aaron.recipeassistant.common.audiocontroller.PermissionsHelper
import com.example.aaron.recipeassistant.common.model.*
import edu.cmu.pocketsphinx.*
import java.io.File
import java.io.IOException
import kotlin.concurrent.thread

typealias ListenerCallback = (UserAction) -> Unit

interface InstructionListener : RecognitionListener {
    fun listen(listenerCallback: ListenerCallback)
    fun stopListening()
}

class InstructionListenerImpl(private val context: Context) : InstructionListener {

    private var recognizer: SpeechRecognizer? = null
    private var callback: ListenerCallback = {}

    private val TAG = "InstructionListener"

    private val permissionsHelper = PermissionsHelper(context as Activity)

    override fun listen(listenerCallback: ListenerCallback) {
        Log.d(TAG, "listen called")
        callback = listenerCallback
        initRecognizer()
    }

    override fun stopListening() {
        detachActivity()
    }

    private fun detachActivity() {
        recognizer?.cancel()
        callback = {}
    }

    private fun initRecognizer() {
        permissionsHelper.requestRecordPermission()
        if (recognizer == null) {
            thread {
                try {
                    val assets = Assets(context)
                    val assetDir = assets.syncAssets()
                    setupRecognizer(assetDir)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            recognizer?.startListening(INSTRUCTION_SEARCH)
        }
    }

    private fun setupRecognizer(assetsDir: File) {
        val instructionsGrammar = File(assetsDir, "keyphrase.list")

        SpeechRecognizerSetup.defaultSetup()
            .setAcousticModel(File(assetsDir, "en-us-ptm"))
            .setDictionary(File(assetsDir, "9711.dict"))
            .recognizer
            .apply {
                addListener(this@InstructionListenerImpl)
                addKeywordSearch(INSTRUCTION_SEARCH, instructionsGrammar)
                startListening(INSTRUCTION_SEARCH)
            }
    }

    override fun onBeginningOfSpeech() {
        Log.i("stt", "beginning of speech")
    }

    override fun onPartialResult(hypothesis: Hypothesis?) {
        if (hypothesis?.hypstr?.contains(STOP) == true) {
            recognizer?.cancel()
            callback(StopReading)
            recognizer?.startListening(INSTRUCTION_SEARCH)
        }
    }

    override fun onEndOfSpeech() {
        recognizer?.stop()
    }

    override fun onResult(hypothesis: Hypothesis?) {
        hypothesis?.hypstr?.let {
            Log.i("stt", "onResult: $it")
            processInstruction(it)
        }
        recognizer?.startListening(INSTRUCTION_SEARCH)
    }

    override fun onError(e: Exception) {}

    override fun onTimeout() {}

    private fun processInstruction(instruction: String) {
        Log.d("stt", "UserAction: ${instruction.trim()}")
        when (instruction.trim()) {
            PLAY_INGREDIENT -> callback(PlayIngredient)
            PREV_INGREDIENT -> callback(PrevIngredient)
            NEXT_INGREDIENT -> callback(NextIngredient)
            NEXT_DIRECTION -> callback(NextDirection)
            PREV_DIRECTION -> callback(PrevDirection)
            PLAY_DIRECTION -> callback(PlayDirection)
            STOP -> callback(StopReading)
            FIRST_DIRECTION -> callback(FirstDirection)
            FINAL_DIRECTION -> callback(FinalDirection)
        }
    }

    companion object {
        private const val INSTRUCTION_SEARCH = "instruction"
        private const val PLAY_INGREDIENT = "PLAY_INGREDIENT"
        private const val PREV_INGREDIENT = "PREVIOUS_INGREDIENT"
        private const val NEXT_INGREDIENT = "NEXT_INGREDIENT"
        private const val PLAY_DIRECTION = "PLAY_DIRECTION"
        private const val PREV_DIRECTION = "PREVIOUS_DIRECTION"
        private const val NEXT_DIRECTION = "NEXT_DIRECTION"
        private const val STOP = "STOP"
        private const val FIRST_DIRECTION = "FIRST_DIRECTION"
        private const val FINAL_DIRECTION = "FINAL_DIRECTION"
    }
}
