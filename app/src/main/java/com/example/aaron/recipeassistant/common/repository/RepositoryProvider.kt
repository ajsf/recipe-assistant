package com.example.aaron.recipeassistant.common.repository

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
import io.reactivex.schedulers.Schedulers

object RepositoryProvider {

    private val service = RecipeService.retrofit.create(RecipeService::class.java)

    private val dtoMapper = RecipeMapper()
    private val recipeMapper = RecipeEntityToRecipeMapper()
    private val listMapper = RecipeListMapper(dtoMapper)

    fun getRepository(context: Context): RecipesRepository {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val api = RecipeApi(service, listMapper, NetworkHelperImpl(connectivityManager))

        val boundaryCallback = RecipeBoundaryCallback(api, Schedulers.io())
        val pagedListBuilder = RecipePagedListBuilder(boundaryCallback)

        val recipeDao = RecipeDatabase.getInstance(context).recipeDao()

        val cache = RecipeCacheImpl(recipeDao, recipeMapper, pagedListBuilder)

        return RecipesRepositoryImpl(cache)
    }
}