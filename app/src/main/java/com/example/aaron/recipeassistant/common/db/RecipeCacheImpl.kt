package com.example.aaron.recipeassistant.common.db

import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.example.aaron.recipeassistant.common.data.RecipePagedListBuilder
import com.example.aaron.recipeassistant.common.db.room.RecipeDao
import com.example.aaron.recipeassistant.common.db.room.RecipeEntity
import com.example.aaron.recipeassistant.common.mapper.Mapper
import com.example.aaron.recipeassistant.common.model.Recipe
import io.reactivex.Flowable
import io.reactivex.Single

interface RecipeCache {
    fun insertRecipes(recipeList: List<Recipe>)
    fun allRecipes(): DataSource.Factory<Int, Recipe>
    fun getRecipeById(id: String): Single<Recipe>
    fun getRecipeFeed(): Flowable<PagedList<Recipe>>
}

class RecipeCacheImpl(
    private val recipeDao: RecipeDao,
    private val recipeMapper: Mapper<RecipeEntity, Recipe>,
    private val pagedListBuilder: RecipePagedListBuilder
) : RecipeCache {

    override fun insertRecipes(recipeList: List<Recipe>) {
        recipeDao.insert(recipeList.map { recipeMapper.toDomain(it) })
    }

    override fun allRecipes(): DataSource.Factory<Int, Recipe> = recipeDao
        .getAllRecipes()
        .map {
            recipeMapper.toModel(it)
        }

    override fun getRecipeById(id: String): Single<Recipe> = recipeDao.getRecipeById(id)
        .map { recipeMapper.toModel(it) }

    override fun getRecipeFeed() = pagedListBuilder.getPagedList(this)
}