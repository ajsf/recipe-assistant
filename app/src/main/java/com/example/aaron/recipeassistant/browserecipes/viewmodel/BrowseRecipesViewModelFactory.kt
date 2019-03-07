package com.example.aaron.recipeassistant.browserecipes.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.net.ConnectivityManager
import com.example.aaron.recipeassistant.common.data.RecipeBoundaryCallback
import com.example.aaron.recipeassistant.common.data.RecipePagedListBuilder
import com.example.aaron.recipeassistant.common.db.RecipeCacheImpl
import com.example.aaron.recipeassistant.common.db.room.RecipeDatabase
import com.example.aaron.recipeassistant.common.mapper.RecipeEntityToRecipeMapper
import com.example.aaron.recipeassistant.common.mapper.RecipeListMapper
import com.example.aaron.recipeassistant.common.mapper.RecipeMapper
import com.example.aaron.recipeassistant.common.networking.NetworkHelperImpl
import com.example.aaron.recipeassistant.common.networking.RecipeApi
import com.example.aaron.recipeassistant.common.networking.RecipeService
import com.example.aaron.recipeassistant.common.repository.RecipesRepositoryImpl
import io.reactivex.schedulers.Schedulers

class BrowseRecipesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val service = RecipeService.retrofit.create(RecipeService::class.java)

        val dtoMapper = RecipeMapper()
        val listMapper = RecipeListMapper(dtoMapper)

        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val api = RecipeApi(service, listMapper, NetworkHelperImpl(connectivityManager))

        val boundaryCallback = RecipeBoundaryCallback(api, Schedulers.io())
        val pagedListBuilder = RecipePagedListBuilder(boundaryCallback)

        val recipeDao = RecipeDatabase.getInstance(context).recipeDao()

        val recipeMapper = RecipeEntityToRecipeMapper()
        val cache = RecipeCacheImpl(recipeDao, recipeMapper, pagedListBuilder)

        val repository = RecipesRepositoryImpl(cache)
        return BrowseRecipesViewModel(repository) as T
    }

}