package com.example.aaron.recipeassistant.browserecipes.networking;

import com.example.aaron.recipeassistant.model.RecipeList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

public interface RecipeService {

    @GET("random.php")
    Call<RecipeList> randomMeal();

    @GET("randomselection.php")
    Call<RecipeList> getRandomSelection();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build();
}
