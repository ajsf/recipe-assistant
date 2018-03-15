package com.example.aaron.recipeassistant.browserecipes

import com.example.aaron.recipeassistant.browserecipes.networking.RecipeService
import com.example.aaron.recipeassistant.model.Recipe
import com.example.aaron.recipeassistant.model.toRecipeList
import io.reactivex.Single

interface RecipesRepository {
    fun getRecipes(): Single<List<Recipe>>
}

class RecipesRepositoryImpl : RecipesRepository {

    private val retrofit = RecipeService.retrofit.create(RecipeService::class.java)

    override fun getRecipes() = retrofit.randomSelection().map { it.toRecipeList() }
}