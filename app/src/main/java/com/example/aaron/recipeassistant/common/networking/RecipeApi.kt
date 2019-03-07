package com.example.aaron.recipeassistant.common.networking

import com.example.aaron.recipeassistant.common.mapper.Mapper
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.model.RecipeListDTO
import io.reactivex.Flowable

data class RecipeApiResponse(val recipeList: List<Recipe>? = null, val error: String? = null)

class RecipeApi(
    private val service: RecipeService,
    private val mapper: Mapper<RecipeListDTO, List<Recipe>>,
    private val networkHelper: NetworkHelper
) {

    private val errorMessage = "No Internet Connection"

    fun getRandomRecipes(): Flowable<RecipeApiResponse> {
        val errorFlowable = errorFlowable()
            .map { RecipeApiResponse(error = errorMessage) }

        val successFlowable = successFlowable()
            .flatMapSingle { service.randomSelection() }
            .filter { it.meals?.isNotEmpty() ?: false }
            .map { mapper.toModel(it) }
            .map { RecipeApiResponse(it) }

        return errorFlowable.mergeWith(successFlowable)
    }


    private fun errorFlowable(): Flowable<Boolean> = getNetworkConnectivity()
        .filter { !it }

    private fun successFlowable(): Flowable<Boolean> = getNetworkConnectivity()
        .filter { it }

    private fun getNetworkConnectivity() = networkHelper.observeNetworkConnectivity()

}

