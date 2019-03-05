package com.example.aaron.recipeassistant.readrecipe.audiocontroller.voicerecognition

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import edu.cmu.pocketsphinx.SpeechRecognizer
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test

class InstructionRecognizerImplTest {

    private lateinit var mockTranslator: InstructionTranslator
    private lateinit var mockBuilder: (String) -> SpeechRecognizer
    private lateinit var mockSpeechRecognizer: SpeechRecognizer


    private lateinit var recognizer: InstructionRecognizer

    @Before
    fun setup() {
        mockTranslator = mock()
        mockBuilder = mock()
        mockSpeechRecognizer = mock()

        whenever(mockBuilder.invoke(any()))
            .thenReturn(mockSpeechRecognizer)

        recognizer = InstructionRecognizerImpl(mockBuilder)
    }

    @Test
    fun `calling startListening calls the speechRecognitionBuilder`() {
        recognizer.startListening(mockTranslator)

        verify(mockBuilder).invoke(any())
    }

    @Test
    fun `calling startListening adds the translator as a listener to the speech recognizer`() {
        recognizer.startListening(mockTranslator)

        verify(mockSpeechRecognizer).addListener(mockTranslator)
    }

    @Test
    fun `calling startListening calls start listening on the speech recognizer`() {
        recognizer.startListening(mockTranslator)

        verify(mockSpeechRecognizer).startListening(any())
    }

    @Test
    fun `calling startListening followed by stop calls removeListener on the recognizer returned by the builder`() {
        recognizer.startListening(mockTranslator)
        recognizer.stop()

        verify(mockSpeechRecognizer).removeListener(mockTranslator)
    }

    @Test
    fun `calling startListening followed by stop calls removeListener on the speech recognizer`() {
        recognizer.startListening(mockTranslator)
        recognizer.stop()

        verify(mockSpeechRecognizer).removeListener(mockTranslator)
    }

    @Test
    fun `calling startListening followed by stop calls cancel on the recognizer returned by the builder`() {
        recognizer.startListening(mockTranslator)
        recognizer.stop()

        verify(mockSpeechRecognizer).cancel()
    }
}