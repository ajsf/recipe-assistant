package com.example.aaron.recipeassistant.readrecipe

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.transition.Fade
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.model.Recipe
import com.example.aaron.recipeassistant.readrecipe.readerservice.RecipeReader
import com.example.aaron.recipeassistant.readrecipe.voicerecognitionservice.InstructionListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_read_recipe.*

class ReadRecipeActivity : AppCompatActivity() {

    private var recipeReader: RecipeReader? = null
    private var instructionListener: InstructionListener? = null
    private lateinit var recipe: Recipe
    private var listening: Boolean = false
    private lateinit var audioManager: AudioManager
    private lateinit var listenerToggleBtn: MenuItem

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (instructionListener != null) {
                    instructionListener!!.destroy()
                }
                recipeReader?.destroy()
                instructionListener = null
                recipeReader = null
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> recipeReader?.stopReading()
            AudioManager.AUDIOFOCUS_GAIN -> {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_recipe)
        recipe = intent.extras.getParcelable("recipe")
        initActivity()
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        displayRecipe(recipe)
    }

    override fun onResume() {
        super.onResume()
        Log.i("ReadRecipeActivity", "OnResume")
        if (recipeReader == null) {
            requestAudioFocus()
        }
    }

    override fun onDestroy() {
        Log.i("ReadRecipeActivity", "onDestroy")
        super.onDestroy()
        if (instructionListener != null) {
            instructionListener?.destroy()
        }
        recipeReader?.destroy()
    }

    private fun initActivity() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        setSupportActionBar(toolbar)
        collapsing_toolbar.setExpandedTitleColor(getColor(R.color.colorLightest))
        initTransitions()
        initButtons()
    }

    private fun initTransitions() {
        postponeEnterTransition()
        val fade = Fade()
        fade.duration = 600
        window.returnTransition = fade
        window.allowReturnTransitionOverlap = true
        recipe_details_layout.alpha = 0.0f
        toolbar.alpha = 0f
        toolbar.title = ""
    }

    private fun initButtons() {
        btn_play_direction.setOnClickListener { recipeReader?.readIngredients() }
        btn_prev_direction.setOnClickListener { recipeReader?.readPreviousDirection() }
        btn_next_direction.setOnClickListener { recipeReader?.readNextDirection() }
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        displayRecipeImage(recipe)
    }

    private fun displayRecipe(recipe: Recipe?) {
        val ingredientsBuilder = StringBuilder()
        val directionsBuilder = StringBuilder()

        recipe?.ingredients?.forEach { ingredientsBuilder.append(it).append("\n") }
        recipe?.directions?.forEach { directionsBuilder.append(it).append("\n\n") }

        val length = directionsBuilder.length
        directionsBuilder.delete(length - 2, length)
        ingredientsBuilder.deleteCharAt(ingredientsBuilder.length - 1)
        ingredients_text.text = ingredientsBuilder.toString()
        directions_text.text = directionsBuilder.toString()
        recipe_details_layout.animate().alpha(1.0f).duration = 1000
        toolbar.animate().alpha(1.0f).setDuration(1000).withEndAction { toolbar.title = recipe?.title?.trim { it <= ' ' }?.toUpperCase() }
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
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestAudioFocus()
            } else {
                finish()
            }
        }
    }

    private fun requestAudioFocus() {
        val result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        when (result) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> buildReader()
        }
    }

    private fun buildReader() {
        recipeReader = RecipeReader(this@ReadRecipeActivity)
        recipeReader?.setRecipe(recipe)
    }

    private fun buildListener() {
        instructionListener = InstructionListener(this@ReadRecipeActivity, recipeReader)
        listening = true
    }

    private fun toggleListener() {
        if (listening) {
            listenerToggleBtn.setIcon(R.drawable.ic_microphone_off_white_36dp)
            instructionListener?.destroy()
            listening = false
        } else {
            listenerToggleBtn.setIcon(R.drawable.ic_microphone_outline_white_36dp)
            requestAudioPermission()
        }
    }

    private fun displayRecipeImage(recipe: Recipe) {
        Picasso.with(this)
                .load(recipe.imageUrl)
                .fit()
                .into(recipe_header_image, object : Callback {
                    override fun onSuccess() {
                        startPostponedEnterTransition()
                    }

                    override fun onError() {
                        startPostponedEnterTransition()
                    }
                })
    }

    companion object {
        private val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }
}
