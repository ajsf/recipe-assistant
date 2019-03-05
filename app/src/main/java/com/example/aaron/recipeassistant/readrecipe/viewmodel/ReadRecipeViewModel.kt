package com.example.aaron.recipeassistant.readrecipe.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.aaron.recipeassistant.common.audiocontroller.AudioController
import com.example.aaron.recipeassistant.common.model.*
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

data class ReadRecipeViewState(
    val isListening: Boolean = false,
    val readingIngredient: Boolean = false,
    val readingDirection: Boolean = false,
    val ingredientIndex: Int = 0,
    val directionIndex: Int = 0,
    val ingredients: List<String> = listOf(),
    val directions: List<String> = listOf()
)

class ReadRecipeViewModel(
    private val audioController: AudioController,
    private val uiScheduler: Scheduler = AndroidSchedulers.mainThread()
) : ViewModel() {

    val viewStateLiveData: LiveData<ReadRecipeViewState>
        get() = _viewStateLiveData

    private val _viewStateLiveData = MutableLiveData<ReadRecipeViewState>()

    private val disposable: CompositeDisposable

    private var userActionSubscription: Disposable? = null
        set(value) {
            if (value != null) disposable.add(value)
            field = value
        }

    init {
        _viewStateLiveData.value = ReadRecipeViewState()
        disposable = CompositeDisposable()
        observeReadingStatus(disposable)
    }

    fun viewAction(userAction: UserAction) = instructionListenerCallback(userAction)

    fun setRecipe(recipe: Recipe) {
        updateViewState(
            ReadRecipeViewState(
                ingredients = recipe.ingredients,
                directions = recipe.directions
            )
        )
    }

    private fun observeReadingStatus(disposable: CompositeDisposable) {
        disposable.add(audioController.readingStatus
            .observeOn(uiScheduler)
            .subscribeBy {
                if (it.not()) {
                    if (getViewState().readingIngredient) {
                        updateReadingIngredient(false)
                    } else {
                        updateReadingDirection(false)
                    }
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    private fun instructionListenerCallback(userAction: UserAction) = when (userAction) {
        PlayIngredient -> readIngredient()
        PrevIngredient -> {
            prevIngredient()
            readIngredient()
        }
        NextIngredient -> {
            nextIngredient()
            readIngredient()
        }
        PlayDirection -> readDirection()
        PrevDirection -> {
            prevDirection()
            readDirection()
        }
        NextDirection -> {
            nextDirection()
            readDirection()
        }
        FirstDirection -> setCurrentDirection(0)
        FinalDirection -> setLastDirection()
        StopReading -> stopReading()
        FirstIngredient -> setCurrentIngredient(0)
        FinalIngredient -> setLastIngredient()
        Listen -> listen()
        StopListening -> stopListening()
        ToggleListener -> toggleListener()
        is SetIngredient -> setCurrentIngredient(userAction.index)
        is SetDirection -> setCurrentDirection(userAction.index)
    }

    private fun listen() {
        userActionSubscription = audioController.listen()
            .observeOn(uiScheduler)
            .doOnSubscribe { updateIsListening(true) }
            .doOnCancel { updateIsListening(false) }
            .doAfterTerminate { updateIsListening(false) }
            .subscribeBy { instructionListenerCallback(it) }
    }

    private fun stopListening() {
        userActionSubscription?.let { disposable.remove(it) }
        userActionSubscription = null
    }

    private fun readIngredient() {
        if (getViewState().readingIngredient.not()) {
            updateReadingIngredient(true)
            audioController.read(getCurrentIngredient())
        } else {
            audioController.stopReading()
            updateReadingIngredient(false)
        }
    }

    private fun readDirection() {
        if (getViewState().readingDirection.not()) {
            updateReadingDirection(true)
            audioController.read(getCurrentDirection())
        } else {
            audioController.stopReading()
            updateReadingDirection(false)
        }
    }

    private fun setCurrentIngredient(index: Int) {
        if (index in getViewState().ingredients.indices) updateIngredient(index)
    }

    private fun nextIngredient() = setCurrentIngredient(getViewState().ingredientIndex + 1)

    private fun prevIngredient() = setCurrentIngredient(getViewState().ingredientIndex - 1)

    private fun setLastIngredient() = updateIngredient(getViewState().ingredients.lastIndex)


    private fun setCurrentDirection(index: Int) {
        if (index in getViewState().directions.indices) updateDirection(index)
    }

    private fun nextDirection() = setCurrentDirection(getViewState().directionIndex + 1)

    private fun prevDirection() = setCurrentDirection(getViewState().directionIndex - 1)

    private fun setLastDirection() = updateDirection(getViewState().directions.lastIndex)

    private fun stopReading() {
        audioController.stopReading()
        setReadingFinished()
    }

    private fun toggleListener() {
        if (getViewState().isListening) {
            stopListening()
        } else {
            listen()
        }
    }

    private fun getCurrentIngredient(): String =
        with(getViewState()) { ingredients.getOrNull(ingredientIndex) ?: "" }

    private fun getCurrentDirection(): String =
        with(getViewState()) { directions.getOrNull(directionIndex) ?: "" }

    private fun updateIsListening(listening: Boolean) =
        updateViewState(getViewState().copy(isListening = listening))

    private fun updateReadingDirection(isReading: Boolean) =
        updateViewState(getViewState().copy(readingDirection = isReading))

    private fun updateReadingIngredient(isReading: Boolean) =
        updateViewState(getViewState().copy(readingIngredient = isReading))

    private fun updateIngredient(index: Int) =
        updateViewState(getViewState().copy(ingredientIndex = index))

    private fun updateDirection(index: Int) =
        updateViewState(getViewState().copy(directionIndex = index))

    private fun updateViewState(newViewState: ReadRecipeViewState) {
        _viewStateLiveData.value = newViewState
    }

    private fun setReadingFinished() = updateViewState(
        getViewState().copy(
            readingIngredient = false,
            readingDirection = false
        )
    )

    private fun getViewState() = _viewStateLiveData.value!!
}