package com.example.aaron.recipeassistant.common.audiocontroller

import com.example.aaron.recipeassistant.common.audiocontroller.reader.ReadingFinishedNotification
import com.example.aaron.recipeassistant.common.audiocontroller.reader.RecipeReader
import com.example.aaron.recipeassistant.common.audiocontroller.voicerecognition.InstructionListener
import com.example.aaron.recipeassistant.common.model.UserAction
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject

interface AudioController {
    val readingStatus: Flowable<Boolean>
    val listeningStatus: Flowable<Boolean>
    val userActionsFeed: Flowable<UserAction>

    fun read(text: String)
    fun stopReading()
    fun listen()
    fun stopListening()
}

class AudioControllerImpl(
    private val recipeReader: RecipeReader,
    private val instructionListener: InstructionListener
) : AudioController, RecipeReader by recipeReader, InstructionListener by instructionListener {

    private val isReadingSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private val isListeningSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private val userActionsSubject: BehaviorSubject<UserAction> = BehaviorSubject.create()

    private val progressListener =
        ReadingFinishedNotification(
            { isReadingSubject.onNext(true) }, { isReadingSubject.onNext(false) })

    override val readingStatus: Flowable<Boolean>
        get() = isReadingSubject
            .toFlowable(BackpressureStrategy.DROP)

    override val listeningStatus: Flowable<Boolean>
        get() = isListeningSubject
            .toFlowable(BackpressureStrategy.DROP)

    override val userActionsFeed: Flowable<UserAction>
        get() = userActionsSubject.toFlowable(BackpressureStrategy.DROP)

    init {
        recipeReader.setProgressListener(progressListener)
        isReadingSubject.onNext(false)
        isListeningSubject.onNext(false)
    }

    private fun instructionListenerCallback(userAction: UserAction) {
        userActionsSubject.onNext(userAction)
    }

    override fun read(text: String) = recipeReader.read(text)

    override fun stopReading() = recipeReader.stopReading()

    override fun listen() {
        instructionListener.listen(::instructionListenerCallback)
        isListeningSubject.onNext(true)
    }

    override fun stopListening() {
        instructionListener.stopListening()
        isListeningSubject.onNext(false)
    }
}