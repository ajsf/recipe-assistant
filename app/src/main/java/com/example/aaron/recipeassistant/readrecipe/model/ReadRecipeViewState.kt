package com.example.aaron.recipeassistant.readrecipe.model

data class ReadRecipeViewState(
    val isListening: Boolean = false,
    val readingIngredient: Boolean = false,
    val readingDirection: Boolean = false,
    val ingredientIndex: Int = 0,
    val directionIndex: Int = 0,
    val ingredients: List<String> = listOf(),
    val directions: List<String> = listOf(),
    val title: String = "",
    val imageUrl: String = ""
)