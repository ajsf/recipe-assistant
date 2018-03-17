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
import android.widget.ImageView
import com.example.aaron.recipeassistant.App
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.model.Recipe
import com.example.aaron.recipeassistant.readrecipe.readerservice.RecipeReader
import com.example.aaron.recipeassistant.readrecipe.voicerecognitionservice.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_read_recipe.*
import kotlinx.android.synthetic.main.read_recipe_toolbar.*
import kotlinx.android.synthetic.main.recipe_card.view.*

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
        viewModel.recipeReader = getRecipeReader()
        viewModel.readingDirection.observe(this, Observer {
            it?.let { setPlayButtonIcon(directions_card.btn_play, it) }
        })
        viewModel.readingIngredient.observe(this, Observer {
            it?.let { setPlayButtonIcon(ingredients_card.btn_play, it) }
        })
        viewModel.recipe.observe(this, Observer {
            it?.let { displayRecipe(it) }
        })
        viewModel.recipe.value = recipe
    }

    private fun getRecipeReader() : RecipeReader {
        val recipeReader = RecipeReader(this)
        lifecycle.addObserver(recipeReader)
        return recipeReader
    }

    private fun setPlayButtonIcon(btn: ImageView, isPlaying: Boolean) {
        if (isPlaying) {
            btn.setImageResource(R.drawable.ic_stop_black_24dp)
        } else {
            btn.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
    }

    private fun initButtons() {
        ingredients_card.btn_play.setOnClickListener { viewModel.readIngredient() }
        ingredients_card.btn_next.setOnClickListener { viewModel.nextIngredient() }
        ingredients_card.btn_prev.setOnClickListener { viewModel.prevIngredient() }
        directions_card.btn_play.setOnClickListener { viewModel.readDirection() }
        directions_card.btn_prev.setOnClickListener { viewModel.prevDirection() }
        directions_card.btn_next.setOnClickListener { viewModel.nextDirection() }
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
        with(ingredients_card) {
            tv_label.text = getString(R.string.ingredients_label)
            tv_text.text = ingredients
            tv_text.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Body2)
        }
        with(directions_card) {
            tv_label.text = getString(R.string.directions_label)
            tv_text.text = directions
        }

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
        private const val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }
}
