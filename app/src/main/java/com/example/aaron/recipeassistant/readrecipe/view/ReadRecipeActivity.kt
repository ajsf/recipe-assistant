package com.example.aaron.recipeassistant.readrecipe.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.transition.Fade
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.common.model.*
import com.example.aaron.recipeassistant.readrecipe.viewmodel.ReadRecipeViewModel
import com.example.aaron.recipeassistant.readrecipe.viewmodel.ReadRecipeViewModelFactory
import com.example.aaron.recipeassistant.readrecipe.viewmodel.ReadRecipeViewState
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_read_recipe.*
import kotlinx.android.synthetic.main.read_recipe_toolbar.*

class ReadRecipeActivity : AppCompatActivity() {

    private var listenerToggleBtn: MenuItem? = null

    private lateinit var viewModel: ReadRecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_recipe)
        initActivity()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_read_recipe, menu)
        listenerToggleBtn = menu.findItem(R.id.action_listen)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            supportFinishAfterTransition()
            return true
        }
        if (item.itemId == R.id.action_listen) {
            viewModel.viewAction(ToggleListener)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        viewModel.viewAction(StopReading)
    }

    private fun initActivity() {
        setSupportActionBar(toolbar)
        initTransitions()
        initViewModel()
        initButtons()
    }

    private fun initTransitions() {
        window.returnTransition = Fade().apply { duration = 600 }
        window.allowReturnTransitionOverlap = true
        recipe_details_layout.alpha = 0.0f
        toolbar.alpha = 0f
        toolbar.title = ""
    }

    private fun initViewModel() {
        val factory = ReadRecipeViewModelFactory(this)

        viewModel = ViewModelProviders.of(this, factory)
            .get(ReadRecipeViewModel::class.java)

        viewModel.viewStateLiveData.observe(this, Observer {
            it?.let { viewState -> render(viewState) }
        })

        val recipe = intent.extras.getParcelable("recipe") as Recipe
        viewModel.setRecipe(recipe)
        displayRecipe(recipe)
    }

    private fun render(viewState: ReadRecipeViewState): Unit = with(viewState) {
        ingredients_card.setPlaying(readingIngredient)
        directions_card.setPlaying(readingDirection)

        ingredients_card.setSelectedItem(ingredientIndex)
        directions_card.setSelectedItem(directionIndex)

        if (isListening) {
            listenerToggleBtn?.setIcon(R.drawable.ic_microphone_outline_white_36dp)
        } else {
            listenerToggleBtn?.setIcon(R.drawable.ic_microphone_off_white_36dp)
        }
    }

    private fun initButtons() {
        fun readIngredient() = viewModel.viewAction(PlayIngredient)
        fun nextIngredient() = viewModel.viewAction(NextIngredient)
        fun prevIngredient() = viewModel.viewAction(PrevIngredient)
        fun readDirection() = viewModel.viewAction(PlayDirection)
        fun nextDirection() = viewModel.viewAction(NextDirection)
        fun prevDirection() = viewModel.viewAction(PrevDirection)

        ingredients_card.initButtons(::readIngredient, ::nextIngredient, ::prevIngredient)
        directions_card.initButtons(::readDirection, ::nextDirection, ::prevDirection)
    }

    private fun displayRecipe(recipe: Recipe) {
        displayRecipeImage(recipe)

        val ingredientsLabel = resources.getString(R.string.ingredients_label)
        val directionsLabel = resources.getString(R.string.directions_label)

        ingredients_card.displayItems(
            recipe.ingredients,
            ingredientsLabel
        )
        { viewModel.viewAction(SetIngredient(it)) }

        directions_card.displayItems(
            recipe.directions,
            directionsLabel
        )
        { viewModel.viewAction(SetDirection(it)) }

        collapsing_toolbar.title = recipe.title.trim { it <= ' ' }.toUpperCase()
        recipe_details_layout.animate().alpha(1.0f).duration = 1000
        toolbar.animate().alpha(1.0f).duration = 1000
    }

    private fun displayRecipeImage(recipe: Recipe) {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = resources.getDimension(R.dimen.collapsing_toolbar_height).toInt()
        Picasso.with(this)
            .load(recipe.imageUrl)
            .resize(width, height)
            .into(recipe_header_image)
    }
}
