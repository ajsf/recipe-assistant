package com.example.aaron.recipeassistant.browserecipes.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.repository.RecipesRepository
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

data class BrowseRecipesViewState(val recipes: List<Recipe>)

class BrowseRecipesViewModel(
    private val repository: RecipesRepository,
    private val scheduler: Scheduler = AndroidSchedulers.mainThread()
) : ViewModel() {

    val viewStateLiveData: LiveData<BrowseRecipesViewState>
        get() = _viewStateLiveData

    private val _viewStateLiveData = MutableLiveData<BrowseRecipesViewState>()

    fun getRecipes() {
        repository.getRecipes()
            .subscribeOn(Schedulers.io())
            .observeOn(scheduler)
            .subscribeBy {
                _viewStateLiveData.postValue(BrowseRecipesViewState(it))
            }
    }
}