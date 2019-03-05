package com.example.aaron.recipeassistant.readrecipe.audiocontroller.voicerecognition

import android.util.Log
import com.example.aaron.recipeassistant.readrecipe.model.*
import edu.cmu.pocketsphinx.Hypothesis
import edu.cmu.pocketsphinx.RecognitionListener
import edu.cmu.pocketsphinx.SpeechRecognizer
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

interface InstructionTranslator : RecognitionListener {
    var recognizer: SpeechRecognizer
    fun listen(): Flowable<UserAction>
}

class InstructionTranslatorImpl : InstructionTranslator {

    private val userActionsSubject: PublishSubject<UserAction> = PublishSubject.create()

    override lateinit var recognizer: SpeechRecognizer

    override fun listen(): Flowable<UserAction> {
        return userActionsSubject.toFlowable(BackpressureStrategy.DROP)
    }

    override fun onBeginningOfSpeech() {
        Log.i("stt", "beginning of speech")
    }

    override fun onPartialResult(hypothesis: Hypothesis?) {
        Log.i("stt", "partial result")
        if (hypothesis?.hypstr?.contains(STOP) == true) {
            recognizer.cancel()
            userActionsSubject.onNext(StopReading)
            recognizer.startListening(INSTRUCTION_SEARCH)
        }
    }

    override fun onEndOfSpeech() {
        recognizer.stop()
    }

    override fun onResult(hypothesis: Hypothesis?) {
        Log.i("stt", "onResult: $hypothesis")

        hypothesis?.hypstr?.let {
            processInstruction(it)
        }
        recognizer.startListening(INSTRUCTION_SEARCH)
    }

    override fun onError(e: Exception) {}

    override fun onTimeout() {}

    private fun processInstruction(instruction: String) {
        Log.d("stt", "UserAction: ${instruction.trim()}")
        when (instruction.trim()) {
            PLAY_INGREDIENT -> userActionsSubject.onNext(PlayIngredient)
            PREV_INGREDIENT -> userActionsSubject.onNext(PrevIngredient)
            NEXT_INGREDIENT -> userActionsSubject.onNext(NextIngredient)
            NEXT_DIRECTION -> userActionsSubject.onNext(NextDirection)
            PREV_DIRECTION -> userActionsSubject.onNext(PrevDirection)
            PLAY_DIRECTION -> userActionsSubject.onNext(PlayDirection)
            STOP -> userActionsSubject.onNext(StopReading)
            FIRST_DIRECTION -> userActionsSubject.onNext(FirstDirection)
            FINAL_DIRECTION -> userActionsSubject.onNext(FinalDirection)
        }
    }

    companion object {
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
