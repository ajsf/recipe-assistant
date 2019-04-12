package com.example.aaron.recipeassistant.browserecipes.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.transition.ChangeBounds
import android.transition.Fade
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.browserecipes.viewmodel.BrowseRecipesViewModel
import com.example.aaron.recipeassistant.browserecipes.viewmodel.BrowseRecipesViewModelFactory
import com.example.aaron.recipeassistant.common.model.Recipe
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_browse_recipies.*

class BrowseRecipesActivity : AppCompatActivity() {

    private lateinit var recipeRecyclerViewAdapter: RecipeAdapter

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
        .of(this, BrowseRecipesViewModelFactory(this))
        .get(BrowseRecipesViewModel::class.java)

    private fun createTransition() {
        window.reenterTransition = Fade().apply { duration = 600 }
        window.exitTransition = Fade().apply { duration = 1500 }
        window.allowReturnTransitionOverlap = true
        window.allowEnterTransitionOverlap = true
    }

    private fun initRecyclerView() {
        val columnCount = resources.getInteger(R.integer.recipe_browse_columns)
        val lm = GridLayoutManager(this, columnCount)
        recipeRecyclerViewAdapter = RecipeAdapter(this)

        with(rv_recipe_browse) {
            layoutManager = lm
            adapter = recipeRecyclerViewAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.recipeList.observe(this, Observer {
            it?.let { recipeList -> render(recipeList) }
        })
    }

    private fun render(recipeList: PagedList<Recipe>) {
        recipeList.onEach { recipe ->
            recipe?.imageUrl?.let {
                Picasso.with(this@BrowseRecipesActivity)
                    .load(it)
                    .fetch()
            }
        }
        recipeRecyclerViewAdapter.submitList(recipeList)
    }
}
