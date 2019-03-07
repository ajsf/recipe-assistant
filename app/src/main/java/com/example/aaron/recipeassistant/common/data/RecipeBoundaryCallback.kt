package com.example.aaron.recipeassistant.common.data

import android.arch.paging.PagedList
import com.example.aaron.recipeassistant.common.db.RecipeCache
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.networking.RecipeApi
import io.reactivex.Scheduler
import kotlin.concurrent.thread

class RecipeBoundaryCallback(
    private val recipeApi: RecipeApi,
    private val scheduler: Scheduler
) : PagedList.BoundaryCallback<Recipe>() {

    lateinit var cache: RecipeCache

    private var requestIsInProgress = false

    override fun onZeroItemsLoaded() = requestAndSave()

    override fun onItemAtEndLoaded(itemAtEnd: Recipe) = requestAndSave()

    private fun requestAndSave() {
        if (requestIsInProgress) return
        requestIsInProgress = true
        val response = recipeApi.getRandomRecipes().subscribeOn(scheduler)
            .filter { it.recipeList != null }
            .blockingFirst()

        response.recipeList?.let {
            thread {
                cache.insertRecipes(it)
                requestIsInProgress = false
            }
        }
    }
}