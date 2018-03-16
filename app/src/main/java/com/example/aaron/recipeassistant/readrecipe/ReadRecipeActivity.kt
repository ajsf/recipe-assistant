package com.example.aaron.recipeassistant.readrecipe

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.transition.Fade
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import com.example.aaron.recipeassistant.App
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.model.Recipe
import com.example.aaron.recipeassistant.readrecipe.voicerecognitionservice.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_read_recipe.*

class ReadRecipeActivity : AppCompatActivity() {

    private var instructionListener: InstructionListener? = null
    private lateinit var audioManager: AudioManager
    private lateinit var listenerToggleBtn: MenuItem
    private lateinit var viewModel: ReadRecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_recipe)
        initActivity()
    }

    override fun onResume() {
        super.onResume()
        initInstructionListener()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopReading()
        instructionListener?.detachActivity()
    }

    private fun initActivity() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        setSupportActionBar(toolbar)
        initTransitions()
        initViewModel()
        initButtons()
    }

    private fun initInstructionListener() {
        val app = application as App
        instructionListener = app.instructionListener
        if (instructionListener?.isListening() == true) {
            instructionListener?.listen { instructionListenerCallback(it) }
        }
    }

    private fun initTransitions() {
        val fade = Fade()
        fade.duration = 600
        window.returnTransition = fade
        window.allowReturnTransitionOverlap = true
        recipe_details_layout.alpha = 0.0f
        toolbar.alpha = 0f
        toolbar.title = ""
    }

    private fun initViewModel() {
        val recipe = intent.extras.getParcelable("recipe") as Recipe
        viewModel = ViewModelProviders.of(this).get(ReadRecipeViewModel::class.java)
        viewModel.readingDirection.observe(this, Observer {
            it?.let { setPlayButtonIcon(btn_play_direction, it) }
        })
        viewModel.readingIngredient.observe(this, Observer {
            it?.let { setPlayButtonIcon(btn_play_ingredient, it) }
        })
        viewModel.recipe.observe(this, Observer {
            it?.let { displayRecipe(it) }
        })
        viewModel.recipe.value = recipe
    }

    private fun setPlayButtonIcon(btn: ImageButton, isPlaying: Boolean) {
        if (isPlaying) {
            btn.setImageResource(R.drawable.ic_stop_black_24dp)
        } else {
            btn.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
    }

    private fun initButtons() {
        btn_play_ingredient.setOnClickListener { viewModel.readIngredient() }
        btn_next_ingredient.setOnClickListener { viewModel.nextIngredient() }
        btn_prev_ingredient.setOnClickListener { viewModel.prevIngredient() }
        btn_play_direction.setOnClickListener { viewModel.readDirection() }
        btn_prev_direction.setOnClickListener { viewModel.prevDirection() }
        btn_next_direction.setOnClickListener { viewModel.nextDirection() }
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
            toggleListener()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayRecipe(recipe: Recipe) {
        displayRecipeImage(recipe)
        val (ingredients, directions) = formatRecipeForDisplay(recipe)
        ingredients_text.text = ingredients
        directions_text.text = directions
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

    private fun formatRecipeForDisplay(recipe: Recipe): Pair<String, String> {
        val ingredientsBuilder = StringBuilder()
        val directionsBuilder = StringBuilder()
        recipe.ingredients.forEach { ingredientsBuilder.append(it).append("\n") }
        recipe.directions.forEach { directionsBuilder.append(it).append("\n\n") }
        val length = directionsBuilder.length
        directionsBuilder.delete(length - 2, length)
        ingredientsBuilder.deleteCharAt(ingredientsBuilder.length - 1)
        return ingredientsBuilder.toString() to directionsBuilder.toString()
    }

    private fun requestAudioPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.RECORD_AUDIO)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_REQUEST_RECORD_AUDIO)
        }
    }

    private fun toggleListener() {
        if (instructionListener?.isListening() == true) {
            listenerToggleBtn.setIcon(R.drawable.ic_microphone_off_white_36dp)
            instructionListener?.stopListening()
        } else {
            listenerToggleBtn.setIcon(R.drawable.ic_microphone_outline_white_36dp)
            requestAudioPermission()
            instructionListener?.listen { instructionListenerCallback(it) }
        }
    }

    private fun instructionListenerCallback(instruction: Instruction) {
        Log.d("ReadActivity", instruction.toString())
        when (instruction) {
            is PlayIngredient -> viewModel.readIngredient()
            is PrevIngredient -> {
                viewModel.prevIngredient()
                viewModel.readIngredient()
            }
            is NextIngredient -> {
                viewModel.nextIngredient()
                viewModel.readIngredient()
            }
            is PlayDirection -> viewModel.readDirection()
            is PrevDirection -> {
                viewModel.prevDirection()
                viewModel.readDirection()
            }
            is NextDirection -> {
                viewModel.nextDirection()
                viewModel.readDirection()
            }
            is FirstDirection -> {
            }
            is FinalDirection -> {
            }
            is Stop -> {
                viewModel.stopReading()
            }
        }
    }

    companion object {
        private val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }
}
