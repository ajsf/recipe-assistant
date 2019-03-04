package com.example.aaron.recipeassistant.common.mapper

import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.model.RecipeDTO
import com.example.aaron.recipeassistant.common.model.RecipeListDTO
import com.example.aaron.recipeassistant.test.data.RecipeDataFactory
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Test

class RecipeListMapperTest {

    private lateinit var mockRecipeMapper: Mapper<RecipeDTO, Recipe>
    private lateinit var mapper: RecipeListMapper

    private lateinit var dto: RecipeListDTO
    private lateinit var recipes: List<Recipe>

    @Before
    fun setup() {
        mockRecipeMapper = mock()
        mapper = RecipeListMapper(mockRecipeMapper)

        dto = RecipeDataFactory.randomRecipeListDTO()
        recipes = dto.meals!!.map { RecipeDataFactory.randomRecipe() }

        dto.meals!!.forEachIndexed { index, recipeDTO ->
            When calling mockRecipeMapper.toModel(recipeDTO) itReturns recipes[index]
        }
    }

    @Test
    fun `it calls toModel on the recipe mapper for each RecipeDTO`() {
        mapper.toModel(dto)

        val meals = dto.meals!!

        meals.forEach {
            Verify on mockRecipeMapper that mockRecipeMapper.toModel(it) was called
        }

        verify(mockRecipeMapper, times(meals.size)).toModel(any())
    }

    @Test
    fun `it returns the recipes from the recipe mapper`() {
        val mappedList = mapper.toModel(dto)

        mappedList.forEachIndexed { index, recipe ->
            recipe shouldBe recipes[index]
        }
    }

    @Test
    fun `toDomain throws an UnsupportedOperationException`() {
        val randomList = RecipeDataFactory.randomRecipeList()
        invoking { mapper.toDomain(randomList) } shouldThrow UnsupportedOperationException::class
    }
}