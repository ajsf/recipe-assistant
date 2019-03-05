package com.example.aaron.recipeassistant.common.audiocontroller

import com.example.aaron.recipeassistant.common.audiocontroller.reader.ReadingFinishedNotification
import com.example.aaron.recipeassistant.common.audiocontroller.reader.RecipeReader
import com.example.aaron.recipeassistant.common.audiocontroller.voicerecognition.InstructionRecognizer
import com.example.aaron.recipeassistant.common.audiocontroller.voicerecognition.InstructionTranslator
import com.example.aaron.recipeassistant.common.model.UserAction
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject

interface AudioController {
    val readingStatus: Flowable<Boolean>
    fun listen(): Flowable<UserAction>
    fun read(text: String)
    fun stopReading()
}

class AudioControllerImpl(
    private val recipeReader: RecipeReader,
    private val recognizer: InstructionRecognizer,
    private val translator: InstructionTranslator
) : AudioController {

    private val isReadingSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    private val progressListener = ReadingFinishedNotification(
        { isReadingSubject.onNext(true) }, { isReadingSubject.onNext(false) }
    )

    override val readingStatus: Flowable<Boolean>
        get() = isReadingSubject
            .toFlowable(BackpressureStrategy.DROP)

    init {
        recipeReader.setProgressListener(progressListener)
        isReadingSubject.onNext(false)
    }

    override fun listen(): Flowable<UserAction> = translator
        .listen()
        .doOnSubscribe { recognizer.startListening(translator) }
        .doOnCancel { recognizer.stop() }

    override fun read(text: String) = recipeReader.read(text)

    override fun stopReading() = recipeReader.stopReading()
}