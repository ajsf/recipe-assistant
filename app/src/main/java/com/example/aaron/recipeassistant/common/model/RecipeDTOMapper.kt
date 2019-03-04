package com.example.aaron.recipeassistant.common.model

fun RecipeListDTO.toRecipeList(): List<Recipe> = meals?.map {
    it.toRecipe()
} ?: emptyList()

private fun RecipeDTO.toRecipe(): Recipe {
    val ingredients = getIngredientPairs()
            .filter {
                it.first.isNullOrBlank().not() && it.second.isNullOrBlank().not()
            }
            .map { "${it.first} ${it.second}" }

    val directions = strInstructions?.split("\\n".toRegex())?.filter { it.isNotBlank() } ?: listOf()

    return Recipe(strMeal ?: "", ingredients, directions, strMealThumb ?: "")
}

private fun RecipeDTO.getIngredientPairs() = listOf(
        strMeasure1 to strIngredient1,
        strMeasure2 to strIngredient2,
        strMeasure3 to strIngredient3,
        strMeasure4 to strIngredient4,
        strMeasure5 to strIngredient5,
        strMeasure6 to strIngredient6,
        strMeasure7 to strIngredient7,
        strMeasure8 to strIngredient8,
        strMeasure9 to strIngredient9,
        strMeasure10 to strIngredient10,
        strMeasure11 to strIngredient11,
        strMeasure12 to strIngredient12,
        strMeasure13 to strIngredient13,
        strMeasure14 to strIngredient14,
        strMeasure15 to strIngredient15,
        strMeasure16 to strIngredient16,
        strMeasure17 to strIngredient17,
        strMeasure18 to strIngredient18,
        strMeasure19 to strIngredient19,
        strMeasure20 to strIngredient20)

