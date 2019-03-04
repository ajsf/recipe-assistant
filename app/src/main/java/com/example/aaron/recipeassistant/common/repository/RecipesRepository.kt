package com.example.aaron.recipeassistant.common.repository

import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.model.toRecipeList
import com.example.aaron.recipeassistant.common.networking.RecipeService
import io.reactivex.Single

interface RecipesRepository {
    fun getRecipes(): Single<List<Recipe>>
}

class RecipesRepositoryImpl :
    RecipesRepository {

    private val retrofit = RecipeService.retrofit?.create(RecipeService::class.java)

    override fun getRecipes(): Single<List<Recipe>> {
        return retrofit?.randomSelection()?.map { it.toRecipeList() }
            ?: Single.error(Throwable("Error"))
    }
}