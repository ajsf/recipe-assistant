package com.example.aaron.recipeassistant.common.mapper

import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.model.RecipeDTO
import com.example.aaron.recipeassistant.common.model.RecipeListDTO

class RecipeListMapper(private val recipeMapper: Mapper<RecipeDTO, Recipe>) :
    Mapper<RecipeListDTO, List<Recipe>> {

    override fun toModel(domain: RecipeListDTO): List<Recipe> {
        return domain.meals
            ?.map { recipeMapper.toModel(it) }
            ?: emptyList()
    }
}