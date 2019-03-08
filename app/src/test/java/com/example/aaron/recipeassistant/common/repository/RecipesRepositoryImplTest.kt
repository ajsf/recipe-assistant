package com.example.aaron.recipeassistant.common.repository

import com.example.aaron.recipeassistant.common.db.RecipeCache
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.model.RecipeListDTO
import com.example.aaron.recipeassistant.test.data.RecipeDataFactory
import com.example.aaron.recipeassistant.test.data.TestDataFactory.randomString
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Test

class RecipesRepositoryImplTest {

    private lateinit var mockCache: RecipeCache
    private lateinit var repository: RecipesRepository

    private lateinit var listDTO: RecipeListDTO
    private lateinit var recipeList: List<Recipe>

    @Before
    fun setup() {
        listDTO = RecipeDataFactory.randomRecipeListDTO()
        recipeList = RecipeDataFactory.randomRecipeList()
        mockCache = mock()
        repository = RecipesRepositoryImpl(mockCache)
    }

    @Test
    fun `when getRecipeFeed is called, it calls getRecipeFeed on the cache`() {
        repository.getRecipeFeed()

        Verify on mockCache that mockCache.getRecipeFeed() was called
    }

    @Test
    fun `when getRecipeById is called, it calls getRecipeById on the cache`() {
        val randomId = randomString()
        repository.getRecipeById(randomId)

        Verify on mockCache that mockCache.getRecipeById(randomId) was called
    }
}