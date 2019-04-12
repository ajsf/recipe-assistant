package com.example.aaron.recipeassistant.common.model

data class Recipe(
    val id: String,
    val title: String,
    val ingredients: List<String>,
    val directions: List<String>,
    val imageUrl: String
)