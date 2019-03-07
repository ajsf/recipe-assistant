package com.example.aaron.recipeassistant.common.db.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "recipe")
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val ingredients: List<String>,
    val directions: List<String>,
    val imageUrl: String
)