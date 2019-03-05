package com.example.aaron.recipeassistant.common.audiocontroller.voicerecognition

import edu.cmu.pocketsphinx.SpeechRecognizer

interface InstructionRecognizer {
    fun startListening(translator: InstructionTranslator)
    fun stop()
}

const val INSTRUCTION_SEARCH = "INSTRUCTION"

class InstructionRecognizerImpl(private val speechRecognizerBuilder: (String) -> SpeechRecognizer) :
    InstructionRecognizer {

    private var translator: InstructionTranslator? = null
    private var recognizer: SpeechRecognizer? = null

    override fun startListening(translator: InstructionTranslator) {
        this.translator = translator
        recognizer = speechRecognizerBuilder(INSTRUCTION_SEARCH)
            .apply {
                addListener(translator)
                startListening(INSTRUCTION_SEARCH)
            }.also { translator.recognizer = it }
    }

    override fun stop() {
        translator?.let { recognizer?.removeListener(it) }
        translator = null
        recognizer?.cancel()
        recognizer = null
    }
}
