package com.example.aaron.recipeassistant.common.repository

import com.example.aaron.recipeassistant.common.mapper.Mapper
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.model.RecipeListDTO
import com.example.aaron.recipeassistant.common.networking.RecipeService
import io.reactivex.Single

interface RecipesRepository {
    fun getRecipes(): Single<List<Recipe>>
}

class RecipesRepositoryImpl(
    private val recipeService: RecipeService,
    private val mapper: Mapper<RecipeListDTO, List<Recipe>>
) :
    RecipesRepository {

    override fun getRecipes(): Single<List<Recipe>> {
        return recipeService.randomSelection().map { mapper.toModel(it) }
            ?: Single.error(Throwable("Error"))
    }
}