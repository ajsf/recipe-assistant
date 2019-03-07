package com.example.aaron.recipeassistant.common.mapper

import com.example.aaron.recipeassistant.common.db.room.RecipeEntity
import com.example.aaron.recipeassistant.common.model.Recipe

class RecipeEntityToRecipeMapper : Mapper<RecipeEntity, Recipe> {
    override fun toModel(domain: RecipeEntity) = with(domain) {
        Recipe(id, title, ingredients, directions, imageUrl)
    }

    override fun toDomain(model: Recipe): RecipeEntity = with(model) {
        RecipeEntity(id, title, ingredients, directions, imageUrl)
    }
}