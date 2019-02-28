package com.example.aaron.recipeassistant.common.networking

import com.example.aaron.recipeassistant.common.model.RecipeListDTO
import io.reactivex.Single

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface RecipeService {

    @GET("randomselection.php")
    fun randomSelection(): Single<RecipeListDTO>

    @GET("random.php")
    fun randomMeal(): Single<RecipeListDTO>

    companion object {
        val retrofit : Retrofit? = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://www.themealdb.com/api/json/v1/1/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
    }
}
