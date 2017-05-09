package com.example.aaron.recipeassistant.Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

/**
 * Created by aaron on 5/7/17.
 */

public interface MealService {

    @GET("random.php")
    Call<MealList> randomMeal();

    @GET("randomselection.php")
    Call<MealList> getRandomSelection();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build();
}
