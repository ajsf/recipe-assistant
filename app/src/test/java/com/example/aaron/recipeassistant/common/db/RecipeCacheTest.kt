package com.example.aaron.recipeassistant.common.db

import android.arch.paging.DataSource
import com.example.aaron.recipeassistant.common.data.RecipePagedListBuilder
import com.example.aaron.recipeassistant.common.db.room.RecipeDao
import com.example.aaron.recipeassistant.common.db.room.RecipeEntity
import com.example.aaron.recipeassistant.common.mapper.Mapper
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.test.data.RecipeDataFactory
import com.example.aaron.recipeassistant.test.data.RecipeDataFactory.randomRecipe
import com.example.aaron.recipeassistant.test.data.RecipeDataFactory.randomRecipeEntity
import com.example.aaron.recipeassistant.test.data.TestDataFactory.randomList
import com.example.aaron.recipeassistant.test.data.TestDataFactory.randomString
import com.example.aaron.recipeassistant.test.data.TestDataSourceFactory
import io.reactivex.Single
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Test

class RecipeCacheTest {

    private lateinit var mockDao: RecipeDao
    private lateinit var mockMapper: Mapper<RecipeEntity, Recipe>
    private lateinit var mockPagedListBuilder: RecipePagedListBuilder
    private lateinit var mockEntityDataSource: DataSource.Factory<Int, RecipeEntity>
    private lateinit var mockDataSource: DataSource.Factory<Int, Recipe>

    private lateinit var cache: RecipeCache

    @Before
    fun setup() {
        mockDao = mock()
        mockMapper = mock()
        mockPagedListBuilder = mock()
        mockEntityDataSource = mock()
        mockDataSource = mock()
        cache = RecipeCacheImpl(mockDao, mockMapper, mockPagedListBuilder)
    }

    @Test
    fun `when insertRecipes is called, it calls toDomain on the mapper for each recipe in the list`() {
        val recipeList = RecipeDataFactory.randomRecipeList()

        cache.insertRecipes(recipeList)

        recipeList.forEach {
            Verify on mockMapper that mockMapper.toDomain(it)
        }
    }

    @Test
    fun `when insertRecipes is called, it calls insert on the dao with the mapped recipe entity list`() {
        val recipeList = RecipeDataFactory.randomRecipeList()

        val recipeEntityList = recipeList.map { RecipeDataFactory.randomRecipeEntity() }

        recipeList.forEachIndexed { index, recipe ->
            When calling mockMapper.toDomain(recipe) itReturns recipeEntityList[index]
        }

        cache.insertRecipes(recipeList)

        Verify on mockDao that mockDao.insert(recipeEntityList)
    }

    @Test
    fun `when allRecipes is called, it calls getAllRecipes on the dao`() {
        val factory = TestDataSourceFactory(randomList(::randomRecipeEntity))

        When calling mockDao.getAllRecipes() itReturns factory

        cache.allRecipes()
        Verify on mockDao that mockDao.getAllRecipes() was called
    }

    @Test
    fun `when getRecipeById is called, it calls getRecipeById on the recipeDao`() {
        val randomId = randomString()
        val recipeEntity = randomRecipeEntity()

        When calling mockDao.getRecipeById(randomId) itReturns Single.just(recipeEntity)

        cache.getRecipeById(randomId)

        Verify on mockDao that mockDao.getRecipeById(randomId) was called
    }

    @Test
    fun `when getRecipeById is is subscribed to, it calls toModel on the mapper with the entity returned by the dao`() {
        val randomId = randomString()
        val recipeEntity = randomRecipeEntity()

        When calling mockDao.getRecipeById(randomId) itReturns Single.just(recipeEntity)

        cache.getRecipeById(randomId).test()

        Verify on mockMapper that mockMapper.toModel(recipeEntity) was called
    }

    @Test
    fun `when getRecipeById is is subscribed to, it returns the mapped entity`() {
        val randomId = randomString()
        val recipeEntity = randomRecipeEntity()
        val randomRecipe = randomRecipe()

        When calling mockDao.getRecipeById(randomId) itReturns Single.just(recipeEntity)
        When calling mockMapper.toModel(recipeEntity) itReturns randomRecipe

        val testSubscriber = cache.getRecipeById(randomId).test()

        testSubscriber.assertValue(randomRecipe)
    }

    @Test
    fun `when getRecipeFeed is called, it calls getPagedList on the paged list builder, passing itself as a parameter`() {
        cache.getRecipeFeed()
        Verify on mockPagedListBuilder that mockPagedListBuilder.getPagedList(cache) was called
    }
}
