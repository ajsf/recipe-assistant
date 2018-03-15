package com.example.aaron.recipeassistant.browserecipes.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Fade
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.browserecipes.RecipesRepositoryImpl
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BrowseRecipesActivity : AppCompatActivity() {

    private var recipeRecyclerViewAdapter: RecipeRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        setContentView(R.layout.activity_browse_recipies)

        val fade = Fade()
        fade.duration = 600
        window.reenterTransition = fade
        postponeEnterTransition()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_browse_recipes)
        val columnCount = resources.getInteger(R.integer.recipe_browse_columns)
        val layoutManager = GridLayoutManager(this, columnCount)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recipeRecyclerViewAdapter = RecipeRecyclerViewAdapter(this, columnCount)
        recyclerView.adapter = recipeRecyclerViewAdapter

        val repository = RecipesRepositoryImpl()
        repository.getRecipes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { recipes ->
                    for (recipe in recipes) {
                        Picasso.with(this@BrowseRecipesActivity).load(recipe.imageUrl).fetch()
                    }
                    recipeRecyclerViewAdapter!!.swapMealList(recipes)
                }
    }

    override fun onStart() {
        super.onStart()
        startPostponedEnterTransition()
    }
}
