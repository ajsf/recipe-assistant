package com.example.aaron.recipeassistant.common.mapper

import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.model.RecipeDTO
import com.example.aaron.recipeassistant.test.data.RecipeDataFactory
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.junit.Test

class RecipeMapperTest {

    @Test
    fun `it maps a RecipeDTO to a Recipe correctly`() {
        val mapper = RecipeMapper()
        val dto = RecipeDataFactory.randomRecipeDTO()
        val recipe = mapper.toModel(dto)

        val expectedRecipe = buildExpectedRecipe(dto)
        recipe shouldEqual expectedRecipe
    }

    private fun buildExpectedRecipe(dto: RecipeDTO): Recipe = with(dto) {
        val expectedIngredients = listOf(
            "$strMeasure1 $strIngredient1",
            "$strMeasure2 $strIngredient2",
            "$strMeasure3 $strIngredient3",
            "$strMeasure4 $strIngredient4",
            "$strMeasure5 $strIngredient5",
            "$strMeasure6 $strIngredient6",
            "$strMeasure7 $strIngredient7",
            "$strMeasure8 $strIngredient8",
            "$strMeasure9 $strIngredient9",
            "$strMeasure10 $strIngredient10",
            "$strMeasure11 $strIngredient11",
            "$strMeasure12 $strIngredient12",
            "$strMeasure13 $strIngredient13",
            "$strMeasure14 $strIngredient14",
            "$strMeasure15 $strIngredient15",
            "$strMeasure16 $strIngredient16",
            "$strMeasure17 $strIngredient17",
            "$strMeasure18 $strIngredient18",
            "$strMeasure19 $strIngredient19",
            "$strMeasure20 $strIngredient20"
        )
        val expectedDirections = strInstructions?.split("\\n".toRegex())
        return@with Recipe(strMeal!!, expectedIngredients, expectedDirections!!, strMealThumb!!)
    }

    @Test
    fun `toDomain throws an UnsupportedOperationException`() {
        val mapper = RecipeMapper()
        val randomRecipe = RecipeDataFactory.randomRecipe()
        invoking { mapper.toDomain(randomRecipe) } shouldThrow UnsupportedOperationException::class
    }
}