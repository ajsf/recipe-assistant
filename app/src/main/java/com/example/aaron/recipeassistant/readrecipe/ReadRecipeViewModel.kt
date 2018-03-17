package com.example.aaron.recipeassistant.readrecipe

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.speech.tts.UtteranceProgressListener
import com.example.aaron.recipeassistant.model.Recipe
import com.example.aaron.recipeassistant.readrecipe.readerservice.RecipeReader

class ReadRecipeViewModel : ViewModel() {

    val readingIngredient = MutableLiveData<Boolean>()
    val readingDirection = MutableLiveData<Boolean>()
    val currentIngredientIndex = MutableLiveData<Int>()
    val currentDirectionIndex = MutableLiveData<Int>()

    private var currentIngredient = ""
    private var currentDirection = ""
    private var ingredientsLength = 0
    private var directionsLength = 0

    val recipe = MutableLiveData<Recipe>()

    var recipeReader: RecipeReader? = null
        set(value) {
            field = value
            field?.utteranceProgressListener = progressListener
        }

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
                ingredientsLength = it.ingredients.lastIndex
                directionsLength = it.directions.lastIndex
            }
        }
        readingIngredient.postValue(false)
        readingDirection.postValue(false)
    }

    fun readIngredient() {
        if (readingIngredient.value == false) {
            readingIngredient.value = true
            recipeReader?.read(currentIngredient)
        } else {
            recipeReader?.stopReading()
            readingIngredient.value = false
        }
    }

    fun nextIngredient() {
        val index = currentIngredientIndex.value ?: -1
        if (index < ingredientsLength) {
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
            recipeReader?.read(currentDirection)
        } else {
            recipeReader?.stopReading()
            readingDirection.value = false
        }
    }

    fun nextDirection() {
        val index = currentDirectionIndex.value ?: -1
        if (index < directionsLength) {
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
        recipeReader?.stopReading()
        readingIngredient.value = false
        readingDirection.value = false
    }
}