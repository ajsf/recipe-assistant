package com.example.aaron.recipeassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import com.example.aaron.recipeassistant.Model.MealList;
import com.example.aaron.recipeassistant.Model.MealService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeRecyclerViewAdapter recipeRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_recipies);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_browse_recipes);
        int columnCount = getResources().getInteger(R.integer.recipe_browse_columns);
        GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recipeRecyclerViewAdapter = new RecipeRecyclerViewAdapter(this, columnCount);
        recyclerView.setAdapter(recipeRecyclerViewAdapter);

        MealService mealService = MealService.retrofit.create(MealService.class);
        Call<MealList> call = mealService.getRandomSelection();
        call.enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                recipeRecyclerViewAdapter.swapMealList(response.body());
            }
            @Override
            public void onFailure(Call<MealList> call, Throwable t) {

            }
        });
    }
}
