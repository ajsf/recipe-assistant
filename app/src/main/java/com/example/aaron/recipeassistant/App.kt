package com.example.aaron.recipeassistant

import android.app.Application
import com.example.aaron.recipeassistant.common.voicerecognitionservice.InstructionListener

class App : Application() {

    val instructionListener = InstructionListener(this)
}