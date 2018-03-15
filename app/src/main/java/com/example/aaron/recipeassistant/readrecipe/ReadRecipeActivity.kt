package com.example.aaron.recipeassistant.readrecipe

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.transition.Fade
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.readrecipe.voicerecognitionservice.InstructionListener
import com.example.aaron.recipeassistant.model.Recipe
import com.example.aaron.recipeassistant.readrecipe.readerservice.RecipeReader
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout

class ReadRecipeActivity : AppCompatActivity() {

    private var recipeReader: RecipeReader? = null

    private var instructionListener: InstructionListener? = null

    private var ingredients: TextView? = null
    private var directions: TextView? = null

    private var recipeImage: ImageView? = null

    private var recipeDetailsLayout: LinearLayout? = null

    private var recipe: Recipe? = null
    private var listening: Boolean = false

    private var audioManager: AudioManager? = null

    private var listenerToggle: MenuItem? = null
    private var toolbar: Toolbar? = null

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (instructionListener != null) {
                    instructionListener!!.destroy()
                }
                recipeReader!!.destroy()
                instructionListener = null
                recipeReader = null
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> recipeReader!!.stopReading()
            AudioManager.AUDIOFOCUS_GAIN -> {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_recipe)

        val fade = Fade()
        fade.duration = 600
        window.returnTransition = fade
        window.allowReturnTransitionOverlap = true
        postponeEnterTransition()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        ingredients = findViewById(R.id.ingredients_text)
        directions = findViewById(R.id.directions_text)

        recipeImage = findViewById(R.id.recipe_header_image)

        recipeDetailsLayout = findViewById(R.id.recipe_details_layout)
        recipeDetailsLayout!!.alpha = 0.0f
        recipe = intent.extras.getParcelable("recipe")

        toolbar = findViewById(R.id.toolbar)
        toolbar!!.alpha = 0f
        toolbar!!.title = ""

        setSupportActionBar(toolbar)

        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbarLayout.setExpandedTitleColor(getColor(R.color.colorLightest))

        initButtons()
        listening = false
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_read_recipe, menu)
        listenerToggle = menu.findItem(R.id.action_listen)
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

    private fun requestAudioPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_REQUEST_RECORD_AUDIO)
            return
        }
        buildListener()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestAudioFocus()
            } else {
                finish()
            }
        }
    }

    private fun buildReader() {
        recipeReader = RecipeReader(this@ReadRecipeActivity)
        recipeReader!!.setRecipe(recipe)
    }

    private fun buildListener() {
        instructionListener = InstructionListener(this@ReadRecipeActivity, recipeReader)
        listening = true
    }

    private fun requestAudioFocus() {
        val result = audioManager!!.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        when (result) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> buildReader()
        }
    }

    private fun initButtons() {
        val btnRepeatDirection = findViewById<ImageButton>(R.id.btn_play_direction)
        btnRepeatDirection.setOnClickListener { recipeReader!!.readIngredients() }
        val btnPrevDirection = findViewById<ImageButton>(R.id.btn_prev_direction)
        btnPrevDirection.setOnClickListener { recipeReader!!.readPreviousDirection() }
        val btnNextDirection = findViewById<ImageButton>(R.id.btn_next_direction)
        btnNextDirection.setOnClickListener { recipeReader!!.readNextDirection() }
    }

    private fun toggleListener() {
        if (listening) {
            listenerToggle!!.setIcon(R.drawable.ic_microphone_off_white_36dp)
            instructionListener!!.destroy()
            listening = false
        } else {
            listenerToggle!!.setIcon(R.drawable.ic_microphone_outline_white_36dp)
            requestAudioPermission()
        }
    }

    private fun displayRecipeImage(recipe: Recipe?) {
        Picasso.with(this)
                .load(recipe!!.imageUrl)
                .fit()
                .into(recipeImage!!, object : Callback {
                    override fun onSuccess() {
                        startPostponedEnterTransition()
                    }

                    override fun onError() {
                        startPostponedEnterTransition()
                    }
                })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        displayRecipeImage(recipe)
    }

    private fun displayRecipe(recipe: Recipe?) {
        val ingredientsBuilder = StringBuilder()
        val directionsBuilder = StringBuilder()

        for (s in recipe!!.ingredients) {
            ingredientsBuilder.append(s).append("\n")
        }
        for (s in recipe.directions) {
            directionsBuilder.append(s).append("\n\n")
        }
        val length = directionsBuilder.length
        directionsBuilder.delete(length - 2, length)
        ingredientsBuilder.deleteCharAt(ingredientsBuilder.length - 1)
        ingredients!!.text = ingredientsBuilder.toString()
        directions!!.text = directionsBuilder.toString()
        recipeDetailsLayout!!.animate().alpha(1.0f).duration = 1000
        toolbar!!.animate().alpha(1.0f).setDuration(1000).withEndAction { toolbar!!.title = recipe.title.trim { it <= ' ' }.toUpperCase() }
    }

    override fun onResume() {
        super.onResume()
        Log.i("ReadRecipeActivity", "OnResume")
        if (recipeReader == null) {
            requestAudioFocus()
        }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        displayRecipe(recipe)
    }

    override fun onDestroy() {
        Log.i("ReadRecipeActivity", "onDestroy")
        super.onDestroy()
        if (instructionListener != null) {
            instructionListener!!.destroy()
        }
        recipeReader!!.destroy()
    }

    companion object {

        private val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }
}
