package com.example.aaron.recipeassistant.browserecipes.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.transition.Fade
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.browserecipes.viewmodel.BrowseRecipesViewModel
import com.example.aaron.recipeassistant.browserecipes.viewmodel.BrowseRecipesViewModelFactory
import com.example.aaron.recipeassistant.browserecipes.viewmodel.BrowseRecipesViewState
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
        viewModel = createViewModel()
        createTransition()
        initRecyclerView()
        observeViewModel()
    }

    private fun createViewModel() = ViewModelProviders
        .of(this, BrowseRecipesViewModelFactory())
        .get(BrowseRecipesViewModel::class.java)
        .apply { getRecipes() }

    private fun createTransition() {
        postponeEnterTransition()
        window.reenterTransition = Fade().apply { duration = 640 }
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
