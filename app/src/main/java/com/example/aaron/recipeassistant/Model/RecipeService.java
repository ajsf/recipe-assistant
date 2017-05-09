package com.example.aaron.recipeassistant.Model;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

public interface RecipeService {

    String url = "http://www.themealdb.com/api/json/v1/1/random.php";

    @GET("api/json/v1/1/random.php")
    Call<Recipe> randomRecipe();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://www.themealdb.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build();

}
