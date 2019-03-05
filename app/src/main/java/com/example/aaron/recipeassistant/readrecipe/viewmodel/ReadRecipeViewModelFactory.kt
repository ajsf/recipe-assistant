package com.example.aaron.recipeassistant.readrecipe.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.example.aaron.recipeassistant.common.audiocontroller.AudioControllerImpl
import com.example.aaron.recipeassistant.common.audiocontroller.reader.RecipeReaderImpl
import com.example.aaron.recipeassistant.common.audiocontroller.voicerecognition.InstructionRecognizerImpl
import com.example.aaron.recipeassistant.common.audiocontroller.voicerecognition.InstructionTranslatorImpl
import com.example.aaron.recipeassistant.common.audiocontroller.voicerecognition.speechRecognizerBuilder

class ReadRecipeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val reader = RecipeReaderImpl(context)

        val instructionTranslator = InstructionTranslatorImpl()
        val instructionRecognizer = InstructionRecognizerImpl(speechRecognizerBuilder(context))

        val audioController =
            AudioControllerImpl(reader, instructionRecognizer, instructionTranslator)

        return ReadRecipeViewModel(audioController) as T
    }

}