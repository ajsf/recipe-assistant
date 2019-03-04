package com.example.aaron.recipeassistant.common.audiocontroller.reader

import android.speech.tts.UtteranceProgressListener

private typealias Action = () -> Unit

class ReadingFinishedNotification(private val startAction: Action, private val endAction: Action) :
    UtteranceProgressListener() {

    override fun onStart(p0: String?) = startAction.invoke()

    override fun onDone(p0: String?) = endAction.invoke()

    override fun onError(p0: String?) {}
}