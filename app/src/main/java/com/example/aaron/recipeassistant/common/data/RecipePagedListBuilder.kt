package com.example.aaron.recipeassistant.common.data

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import com.example.aaron.recipeassistant.common.db.RecipeCache
import com.example.aaron.recipeassistant.common.model.Recipe
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class RecipePagedListBuilder(private val boundaryCallback: RecipeBoundaryCallback) {

    private val pageSize = 5

    fun getPagedList(cache: RecipeCache): Flowable<PagedList<Recipe>> {

        boundaryCallback.cache = cache

        val sourceFactory = cache.allRecipes()

        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 3)
            .setEnablePlaceholders(true)
            .build()

        return RxPagedListBuilder(sourceFactory, config)
            .setBoundaryCallback(boundaryCallback)
            .buildFlowable(BackpressureStrategy.DROP)
    }
}