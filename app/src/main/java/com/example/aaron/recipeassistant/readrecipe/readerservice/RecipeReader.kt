package com.example.aaron.recipeassistant.readrecipe.readerservice

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*
import kotlin.collections.HashMap

class RecipeReader(private val context: Context, private val listener: UtteranceProgressListener) : LifecycleObserver {

    private var tts: TextToSpeech? = null

    init {
        initTts()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun initTts() {
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            onTtsCreated(status)
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun activityResumed() {
        if (tts == null) {
            initTts()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun pause() {
        tts?.stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun destroyTts() {
        tts = null
    }

    private fun onTtsCreated(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This language is not supported")
            }
            //val voices = tts?.voices
            //val voiceName = "en-us-x-sfg#female_2-local"
            //val voice = voices?.firstOrNull { it.name == voiceName }
            //tts?.voice = voice
            tts?.setOnUtteranceProgressListener(listener)

        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    fun read(text: String) {
        val map = HashMap<String, String>()
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Speaking")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, map)
    }

    fun stopReading() {
        tts?.stop()

    }
}
