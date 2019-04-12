package com.example.aaron.recipeassistant.browserecipes.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.example.aaron.recipeassistant.common.repository.RepositoryProvider

class BrowseRecipesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = RepositoryProvider.getRepository(context)
        return BrowseRecipesViewModel(repository) as T
    }

}