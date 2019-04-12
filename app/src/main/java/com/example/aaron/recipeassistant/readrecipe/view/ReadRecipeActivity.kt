package com.example.aaron.recipeassistant.readrecipe.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeBounds
import android.transition.Fade
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.readrecipe.model.*
import com.example.aaron.recipeassistant.readrecipe.viewmodel.ReadRecipeViewModel
import com.example.aaron.recipeassistant.readrecipe.viewmodel.ReadRecipeViewModelFactory
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_read_content.*
import kotlinx.android.synthetic.main.read_recipe_toolbar.*

const val RECIPE_ID_EXTRA = "RECIPE_ID"

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initTransitions()
        initViewModel()
    }

    private fun initTransitions() {
        postponeEnterTransition()
        window.returnTransition = Fade().apply { duration = 600 }
    }

    private fun initViewModel() {
        val factory = ReadRecipeViewModelFactory(this)

        viewModel = ViewModelProviders.of(this, factory)
            .get(ReadRecipeViewModel::class.java)

        viewModel.viewStateLiveData.observe(this, Observer {
            it?.let { viewState ->
                if (viewState.directions.isNotEmpty() && viewState.ingredients.isNotEmpty()) {
                    render(viewState)
                }
            }
        })

        val recipeId = intent.extras.getString(RECIPE_ID_EXTRA)
        viewModel.getRecipe(recipeId)
    }

    private fun render(viewState: ReadRecipeViewState): Unit = with(viewState) {
        startPostponedEnterTransition()
        displayRecipe()

        ingredients_card.setPlaying(readingIngredient)
        directions_card.setPlaying(readingDirection)

        ingredients_card.setSelectedItem(ingredientIndex)
        directions_card.setSelectedItem(directionIndex)

        val listenerToggleIcon = if (isListening) R.drawable.ic_microphone_outline_white_36dp
        else R.drawable.ic_microphone_off_white_36dp

        listenerToggleBtn?.setIcon(listenerToggleIcon)
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

    private fun ReadRecipeViewState.displayRecipe() {
        displayImage(imageUrl)
        ingredients_card.displayItems(ingredients) { viewModel.viewAction(SetIngredient(it)) }
        directions_card.displayItems(directions) { viewModel.viewAction(SetDirection(it)) }
        collapsing_toolbar.title = title.trim { it <= ' ' }.toUpperCase()
        initButtons()
    }

    private fun displayImage(url: String) {
        if (url.isNotBlank()) {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)

            val width = metrics.widthPixels
            val height = resources.getDimension(R.dimen.collapsing_toolbar_height).toInt()

            Picasso.with(this@ReadRecipeActivity)
                .load(url)
                .resize(width, height)
                .into(recipe_header_image, object : Callback {
                    override fun onSuccess() {
                        startPostponedEnterTransition()
                    }

                    override fun onError() {
                    }
                })
        }
    }
}
