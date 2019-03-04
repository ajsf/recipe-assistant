package com.example.aaron.recipeassistant.browserecipes.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.aaron.recipeassistant.common.repository.RecipesRepositoryImpl

class BrowseRecipesViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = RecipesRepositoryImpl()
        return BrowseRecipesViewModel(repository) as T
    }

}