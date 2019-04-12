package com.example.aaron.recipeassistant.browserecipes.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.repository.RecipesRepository

class BrowseRecipesViewModel(
    repository: RecipesRepository
) : ViewModel() {

    private val recipeFeed = repository.getRecipeFeed()

    val recipeList: LiveData<PagedList<Recipe>> =
        LiveDataReactiveStreams.fromPublisher(recipeFeed)

}