package com.example.aaron.recipeassistant.test.data

import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.model.RecipeDTO
import com.example.aaron.recipeassistant.common.model.RecipeListDTO
import com.example.aaron.recipeassistant.test.data.TestDataFactory.randomList
import com.example.aaron.recipeassistant.test.data.TestDataFactory.randomString

object RecipeDataFactory {

    fun randomRecipeListDTO() = RecipeListDTO(
        randomList(::randomRecipeDTO)
    )

    fun randomRecipeList() = randomList(::randomRecipe)

    fun randomRecipeDTO() = RecipeDTO(
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString(),
        randomString()
    )

    fun randomRecipe() = Recipe(
        randomString(),
        randomList(::randomString),
        randomList(::randomString),
        randomString()
    )

}