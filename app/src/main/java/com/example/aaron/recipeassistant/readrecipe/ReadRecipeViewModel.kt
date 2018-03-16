package com.example.aaron.recipeassistant.readrecipe

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.speech.tts.UtteranceProgressListener
import com.example.aaron.recipeassistant.model.Recipe
import com.example.aaron.recipeassistant.readrecipe.readerservice.RecipeReader

class ReadRecipeViewModel(application: Application) : AndroidViewModel(application) {

    val readingIngredient = MutableLiveData<Boolean>()
    val readingDirection = MutableLiveData<Boolean>()
    private val currentIngredientIndex = MutableLiveData<Int>()
    private val currentDirectionIndex = MutableLiveData<Int>()

    private var currentIngredient = ""
    private var currentDirection = ""

    val recipe = MutableLiveData<Recipe>()

    private val progressListener = object : UtteranceProgressListener() {
        override fun onDone(p0: String?) {
            if (readingDirection.value == true) readingDirection.postValue(false)
            if (readingIngredient.value == true) readingIngredient.postValue(false)
        }

        override fun onError(p0: String?) {
        }

        override fun onStart(p0: String?) {
        }
    }

    private val recipeReader = RecipeReader(application.applicationContext, progressListener)

    init {
        currentDirectionIndex.observeForever {
            it?.let { index ->
                currentDirection = recipe.value?.directions?.get(index) ?: ""
            }
        }
        currentIngredientIndex.observeForever {
            it?.let { index ->
                currentIngredient = recipe.value?.ingredients?.get(index) ?: ""
            }
        }
        recipe.observeForever {
            it?.let {
                currentIngredientIndex.value = 0
                currentDirectionIndex.value = 0
            }
        }
        readingIngredient.postValue(false)
        readingDirection.postValue(false)
    }

    fun readIngredient() {
        if (readingIngredient.value == false) {
            readingIngredient.value = true
            recipeReader.read(currentIngredient)
        } else {
            recipeReader.stopReading()
            readingIngredient.value = false
        }
    }

    fun nextIngredient() {
        val index = currentIngredientIndex.value ?: -1
        if (index < recipe.value?.ingredients?.lastIndex ?: -1) {
            currentIngredientIndex.value = index + 1
        }
    }

    fun prevIngredient() {
        val index = currentIngredientIndex.value ?: 0
        if (index > 0) {
            currentIngredientIndex.value = index - 1
        }
    }

    fun readDirection() {
        if (readingDirection.value == false) {
            readingDirection.value = true
            recipeReader.read(currentDirection)
        } else {
            recipeReader.stopReading()
            readingDirection.value = false
        }
    }

    fun nextDirection() {
        val index = currentDirectionIndex.value ?: -1
        if (index < recipe.value?.ingredients?.lastIndex ?: -1) {
            currentDirectionIndex.value = index + 1
        }
    }

    fun prevDirection() {
        val index = currentDirectionIndex.value ?: 0
        if (index > 0) {
            currentDirectionIndex.value = index - 1
        }
    }

    fun stopReading() {
        recipeReader.stopReading()
        readingIngredient.value = false
        readingDirection.value = false
    }
}