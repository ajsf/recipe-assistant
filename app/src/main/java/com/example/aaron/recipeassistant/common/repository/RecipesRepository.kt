package com.example.aaron.recipeassistant.common.repository

import android.arch.paging.PagedList
import com.example.aaron.recipeassistant.common.db.RecipeCache
import com.example.aaron.recipeassistant.common.model.Recipe
import io.reactivex.Flowable
import io.reactivex.Single

interface RecipesRepository {
    fun getRecipeFeed(): Flowable<PagedList<Recipe>>
    fun getRecipeById(recipeId: String): Single<Recipe>
}

class RecipesRepositoryImpl(
    private val recipeCache: RecipeCache
) : RecipesRepository {

    override fun getRecipeFeed(): Flowable<PagedList<Recipe>> =
        recipeCache.getRecipeFeed()

    override fun getRecipeById(recipeId: String): Single<Recipe> =
        recipeCache.getRecipeById(recipeId)

}