package com.example.aaron.recipeassistant.browserecipes.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.transition.Fade
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.browserecipes.viewmodel.BrowseRecipesViewModel
import com.example.aaron.recipeassistant.browserecipes.viewmodel.BrowseRecipesViewState
import com.example.aaron.recipeassistant.browserecipes.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_browse_recipies.*

class BrowseRecipesActivity : AppCompatActivity() {

    private lateinit var recipeRecyclerViewAdapter: RecipeRecyclerViewAdapter

    private lateinit var viewModel: BrowseRecipesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_recipies)
        initActivity()
    }

    private fun initActivity() {
        initViewModel()
        createTransitions()
        initRecyclerView()
        observeViewModel()
    }

    private fun initViewModel() {
        val factory = ViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(BrowseRecipesViewModel::class.java)
        viewModel.getRecipes()
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

    private fun observeViewModel() {
        viewModel.viewStateLiveData.observe(this,
            Observer { it?.let { viewState -> render(viewState) } })
        startPostponedEnterTransition()
    }

    private fun render(viewState: BrowseRecipesViewState) = with(viewState) {
        recipes.onEach { recipe ->
            Picasso.with(this@BrowseRecipesActivity)
                .load(recipe.imageUrl)
                .fetch()
        }
        recipeRecyclerViewAdapter.swapMealList(recipes)
    }
}
