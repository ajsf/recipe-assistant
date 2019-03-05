package com.example.aaron.recipeassistant.readrecipe.audiocontroller

import com.example.aaron.recipeassistant.readrecipe.audiocontroller.reader.RecipeReader
import com.example.aaron.recipeassistant.readrecipe.audiocontroller.voicerecognition.InstructionRecognizer
import com.example.aaron.recipeassistant.readrecipe.audiocontroller.voicerecognition.InstructionTranslator
import com.example.aaron.recipeassistant.test.data.TestDataFactory
import com.example.aaron.recipeassistant.test.data.UserActionDataFactory
import io.reactivex.rxkotlin.toFlowable
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Test

class AudioControllerImplTest {

    private lateinit var mockReader: RecipeReader
    private lateinit var mockRecognizer: InstructionRecognizer
    private lateinit var mockTranslator: InstructionTranslator
    private lateinit var audioController: AudioController

    @Before
    fun setup() {
        mockReader = mock()
        mockRecognizer = mock()
        mockTranslator = mock()
        audioController = AudioControllerImpl(mockReader, mockRecognizer, mockTranslator)
    }

    @Test
    fun `calling read calls read on the reader with the text`() {
        val randomText = TestDataFactory.randomString()
        audioController.read(randomText)

        Verify on mockReader that mockReader.read(randomText) was called
    }

    @Test
    fun `calling stopReading calls stopReading on the reader`() {
        audioController.stopReading()

        Verify on mockReader that mockReader.stopReading() was called
    }

    @Test
    fun `the readingStatus flowable is set to false when first created`() {
        val testSubscriber = audioController.readingStatus.test()

        testSubscriber.assertValue(false)
    }

    @Test
    fun `calling listen calls listen on the translator`() {
        When calling mockTranslator.listen() itReturns createActions().toFlowable()

        audioController.listen()

        Verify on mockTranslator that mockTranslator.listen() was called
    }

    @Test
    fun `subscribing  to the flowable returned by listen calls startListening on the recognizer`() {
        When calling mockTranslator.listen() itReturns createActions().toFlowable()

        VerifyNotCalled on mockRecognizer that mockRecognizer.startListening(mockTranslator)

        audioController.listen().test()

        Verify on mockRecognizer that mockRecognizer.startListening(mockTranslator) was called
    }

    @Test
    fun `the flowable returned by listen sends UserActions sent by the translator`() {
        val actions = createActions()
        When calling mockTranslator.listen() itReturns actions.toFlowable()

        val testSubscriber = audioController.listen().test()

        testSubscriber.assertValueSequence(actions)
    }

    @Test
    fun `canceling the subscription to the flowable returned by listen calls stop on the recognizer`() {
        When calling mockTranslator.listen() itReturns createActions().toFlowable()

        VerifyNotCalled on mockRecognizer that mockRecognizer.stop()

        audioController.listen().test().cancel()

        Verify on mockRecognizer that mockRecognizer.stop() was called
    }

    private fun createActions() = UserActionDataFactory.randomUserActionList()
}