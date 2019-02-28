package com.example.aaron.recipeassistant.browserecipes.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.transition.Fade
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.common.RecipesRepositoryImpl
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_browse_recipies.*

class BrowseRecipesActivity : AppCompatActivity() {

    private lateinit var recipeRecyclerViewAdapter: RecipeRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_recipies)
        initActivity()
    }

    override fun onStart() {
        super.onStart()
        startPostponedEnterTransition()
    }

    private fun initActivity() {
        createTransitions()
        initRecyclerView()
        fetchRecipes()
    }

    private fun createTransitions() {
        postponeEnterTransition()
        val fade = Fade()
        fade.duration = 640
        window.reenterTransition = fade
    }

    private fun initRecyclerView() {
        val columnCount = resources.getInteger(R.integer.recipe_browse_columns)
        val lm = GridLayoutManager(this, columnCount)
        recipeRecyclerViewAdapter =
            RecipeRecyclerViewAdapter(
                this,
                columnCount
            )
        with(rv_recipe_browse) {
            layoutManager = lm
            setHasFixedSize(true)
            adapter = recipeRecyclerViewAdapter
        }
    }

    private fun fetchRecipes() {
        val repository = RecipesRepositoryImpl()
        repository.getRecipes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { recipes ->
                    for (recipe in recipes) {
                        Picasso.with(this@BrowseRecipesActivity).load(recipe.imageUrl).fetch()
                    }
                    recipeRecyclerViewAdapter.swapMealList(recipes)
                }
    }
}
