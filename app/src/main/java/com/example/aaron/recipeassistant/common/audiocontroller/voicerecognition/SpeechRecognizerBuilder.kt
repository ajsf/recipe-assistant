package com.example.aaron.recipeassistant.common.audiocontroller.voicerecognition

import android.app.Activity
import android.content.Context
import com.example.aaron.recipeassistant.common.audiocontroller.PermissionsHelper
import edu.cmu.pocketsphinx.Assets
import edu.cmu.pocketsphinx.SpeechRecognizer
import edu.cmu.pocketsphinx.SpeechRecognizerSetup
import java.io.File

fun speechRecognizerBuilder(context: Context): (String) -> SpeechRecognizer = { keyword ->
    val permissionsHelper = PermissionsHelper(context as Activity)
    permissionsHelper.requestRecordPermission()

    val assets = Assets(context)
    val assetDir = assets.syncAssets()
    val instructionsGrammar = File(assetDir, "keyphrase.list")

    SpeechRecognizerSetup.defaultSetup()
        .setAcousticModel(File(assetDir, "en-us-ptm"))
        .setDictionary(File(assetDir, "9711.dict"))
        .recognizer
        .apply {
            addKeywordSearch(keyword, instructionsGrammar)
        }
}