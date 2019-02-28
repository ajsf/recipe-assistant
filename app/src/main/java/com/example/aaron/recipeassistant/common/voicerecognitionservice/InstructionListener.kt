package com.example.aaron.recipeassistant.common.voicerecognitionservice

import android.app.Application
import android.util.Log
import edu.cmu.pocketsphinx.*
import java.io.File
import java.io.IOException
import kotlin.concurrent.thread

class InstructionListener(private val applicationContext: Application) : RecognitionListener {

    private var recognizer: SpeechRecognizer? = null
    private var callback: (Instruction) -> Unit = {}

    private var listening: Boolean = false

    fun isListening() = listening

    fun listen(listenerCallback: (Instruction) -> Unit) {
        listening = true
        callback = listenerCallback
        initRecognizer()
    }

    fun stopListening() {
        listening = false
        detachActivity()
    }

    fun detachActivity() {
        recognizer?.cancel()
        callback = {}
    }

    private fun initRecognizer() {
        if (recognizer == null) {
            thread {
                try {
                    val assets = Assets(applicationContext)
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
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(File(assetsDir, "en-us-ptm"))
                .setDictionary(File(assetsDir, "9711.dict"))
                .recognizer

        recognizer?.addListener(this)
        val instructionsGrammar = File(assetsDir, "keyphrase.list")
        recognizer?.addKeywordSearch(INSTRUCTION_SEARCH, instructionsGrammar)
        recognizer?.startListening(INSTRUCTION_SEARCH)
    }

    override fun onBeginningOfSpeech() {
        Log.i("stt", "beginning of speech")
    }

    override fun onPartialResult(hypothesis: Hypothesis?) {
        if (hypothesis?.hypstr?.contains(STOP) == true) {
            recognizer?.cancel()
            callback(Stop())
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
        Log.d("stt", "Instruction: ${instruction.trim()}")
        when (instruction.trim()) {
            PLAY_INGREDIENT -> callback(PlayIngredient())
            PREV_INGREDIENT -> callback(PrevIngredient())
            NEXT_INGREDIENT -> callback(NextIngredient())
            NEXT_DIRECTION -> callback(NextDirection())
            PREV_DIRECTION -> callback(PrevDirection())
            PLAY_DIRECTION -> callback(PlayDirection())
            STOP -> callback(Stop())
            FIRST_DIRECTION -> callback(FirstDirection())
            FINAL_DIRECTION -> callback(FinalDirection())
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

sealed class Instruction
class PrevIngredient : Instruction()
class NextIngredient : Instruction()
class PlayIngredient : Instruction()
class PrevDirection : Instruction()
class NextDirection : Instruction()
class FirstDirection : Instruction()
class FinalDirection : Instruction()
class PlayDirection : Instruction()
class Stop : Instruction()
