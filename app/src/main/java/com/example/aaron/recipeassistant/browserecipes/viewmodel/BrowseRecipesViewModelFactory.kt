package com.example.aaron.recipeassistant.browserecipes.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.aaron.recipeassistant.common.mapper.RecipeListMapper
import com.example.aaron.recipeassistant.common.mapper.RecipeMapper
import com.example.aaron.recipeassistant.common.networking.RecipeService
import com.example.aaron.recipeassistant.common.repository.RecipesRepositoryImpl

class BrowseRecipesViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val mapper = RecipeListMapper(RecipeMapper())
        val service = RecipeService.retrofit.create(RecipeService::class.java)
        val repository = RecipesRepositoryImpl(service, mapper)
        return BrowseRecipesViewModel(repository) as T
    }

}