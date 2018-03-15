package com.example.aaron.recipeassistant.browserecipes.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;

import com.example.aaron.recipeassistant.model.RecipeDTO;
import com.example.aaron.recipeassistant.model.RecipeList;
import com.example.aaron.recipeassistant.browserecipes.networking.RecipeService;
import com.example.aaron.recipeassistant.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseRecipesActivity extends AppCompatActivity {

    private RecipeRecyclerViewAdapter recipeRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setContentView(R.layout.activity_browse_recipies);

        Fade fade = new Fade();
        fade.setDuration(600);
        getWindow().setReenterTransition(fade);
        postponeEnterTransition();

        RecyclerView recyclerView = findViewById(R.id.recyclerview_browse_recipes);
        int columnCount = getResources().getInteger(R.integer.recipe_browse_columns);
        GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recipeRecyclerViewAdapter = new RecipeRecyclerViewAdapter(this, columnCount);
        recyclerView.setAdapter(recipeRecyclerViewAdapter);

        RecipeService recipeService = RecipeService.retrofit.create(RecipeService.class);
        Call<RecipeList> call = recipeService.getRandomSelection();
        call.enqueue(new Callback<RecipeList>() {
            @Override
            public void onResponse(Call<RecipeList> call, Response<RecipeList> response) {
                RecipeList recipeList = response.body();
                List<RecipeDTO> meals = recipeList.getMeals();
                for (RecipeDTO meal : meals) {
                    Picasso.with(BrowseRecipesActivity.this).load(meal.getStrMealThumb()).fetch();
                }
                recipeRecyclerViewAdapter.swapMealList(recipeList);
            }
            @Override
            public void onFailure(Call<RecipeList> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startPostponedEnterTransition();
    }
}
