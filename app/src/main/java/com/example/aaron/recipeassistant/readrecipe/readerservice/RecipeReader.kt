package com.example.aaron.recipeassistant.readrecipe.readerservice

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

class RecipeReader(private val context: Context) : LifecycleObserver {

    private var tts: TextToSpeech? = null
    lateinit var utteranceProgressListener: UtteranceProgressListener

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun initTts() {
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            onTtsCreated(status)
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun pause() {
        tts?.stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun destroyTts() {
        tts?.shutdown()
        tts = null
    }

    private fun onTtsCreated(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This language is not supported")
            }
            tts?.setOnUtteranceProgressListener(utteranceProgressListener)

        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    fun read(text: String) {
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Speaking")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "Speaking")
    }

    fun stopReading() {
        tts?.stop()
    }
}
