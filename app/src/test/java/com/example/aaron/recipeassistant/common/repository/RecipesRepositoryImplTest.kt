package com.example.aaron.recipeassistant.common.repository

import com.example.aaron.recipeassistant.common.mapper.Mapper
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.model.RecipeListDTO
import com.example.aaron.recipeassistant.common.networking.RecipeService
import com.example.aaron.recipeassistant.test.data.RecipeDataFactory
import io.reactivex.Single
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Test

class RecipesRepositoryImplTest {

    private lateinit var mockService: RecipeService
    private lateinit var mockMapper: Mapper<RecipeListDTO, List<Recipe>>
    private lateinit var repository: RecipesRepository

    private lateinit var listDTO: RecipeListDTO
    private lateinit var recipeList: List<Recipe>

    @Before
    fun setup() {
        listDTO = RecipeDataFactory.randomRecipeListDTO()
        recipeList = RecipeDataFactory.randomRecipeList()
        mockService = mock()
        mockMapper = mock()
        When calling mockService.randomSelection() itReturns Single.just(listDTO)
        When calling mockMapper.toModel(listDTO) itReturns recipeList
        repository = RecipesRepositoryImpl(mockService, mockMapper)
    }

    @Test
    fun `when getRecipes is called, it calls randomSelection on the recipe service`() {
        repository.getRecipes()
        Verify on mockService that mockService.randomSelection() was called
    }

    @Test
    fun `when getRecipes is subscribed to, it toModel on the mapper with the dto returned by the service`() {
        repository.getRecipes().test()
        Verify on mockMapper that mockMapper.toModel(listDTO) was called
    }

    @Test
    fun `when getRecipes is subscribed, it returns the RecipeListDTO returned by the service`() {
        val testSubscriber = repository.getRecipes().test()
        testSubscriber.assertValue(recipeList)
    }

    @Test
    fun `if there is an error in the service it returns an error single`() {
        When calling mockService.randomSelection() itReturns Single.error(Throwable())
        val testSubscriber = repository.getRecipes().test()
        testSubscriber.assertError(Throwable::class.java)
    }

    @Test
    fun `if the mapper throws an exception it returns an error single`() {
        When calling mockMapper.toModel(listDTO) itThrows RuntimeException()
        val testSubscriber = repository.getRecipes().test()
        testSubscriber.assertError(Throwable::class.java)
    }
}