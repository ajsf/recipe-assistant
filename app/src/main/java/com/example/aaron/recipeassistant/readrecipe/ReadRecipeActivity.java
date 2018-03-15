package com.example.aaron.recipeassistant.readrecipe;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aaron.recipeassistant.R;
import com.example.aaron.recipeassistant.readrecipe.voicerecognitionservice.InstructionListener;
import com.example.aaron.recipeassistant.model.Recipe;
import com.example.aaron.recipeassistant.readrecipe.readerservice.RecipeReader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

public class ReadRecipeActivity extends AppCompatActivity {

    private RecipeReader recipeReader;

    private InstructionListener instructionListener;

    private TextView ingredients;
    private TextView directions;

    private ImageView recipeImage;

    private LinearLayout recipeDetailsLayout;

    private Recipe recipe;
    private boolean listening;

    private AudioManager audioManager;

    private MenuItem listenerToggle;
    private Toolbar toolbar;

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_recipe);

        Fade fade = new Fade();
        fade.setDuration(600);
        getWindow().setReturnTransition(fade);
        getWindow().setAllowReturnTransitionOverlap(true);
        postponeEnterTransition();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        ingredients = findViewById(R.id.ingredients_text);
        directions = findViewById(R.id.directions_text);

        recipeImage = findViewById(R.id.recipe_header_image);

        recipeDetailsLayout = findViewById(R.id.recipe_details_layout);
        recipeDetailsLayout.setAlpha(0.0f);
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setAlpha(0f);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getColor(R.color.colorLightest));

        initButtons();
        listening = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read_recipe, menu);
        listenerToggle = menu.findItem(R.id.action_listen);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        if (item.getItemId() == R.id.action_listen) {
            toggleListener();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestAudioPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        buildListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestAudioFocus();
            } else {
                finish();
            }
        }
    }

    private void buildReader() {
        recipeReader = new RecipeReader(ReadRecipeActivity.this);
        recipeReader.setRecipe(recipe);
    }

    private void buildListener() {
        instructionListener = new InstructionListener(ReadRecipeActivity.this, recipeReader);
        listening = true;
    }

    private void requestAudioFocus() {
        int result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        switch (result) {
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                buildReader();
                break;
        }
    }

    private void initButtons() {
        ImageButton btnRepeatDirection = findViewById(R.id.btn_play_direction);
        btnRepeatDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeReader.readIngredients();
            }
        });
        ImageButton btnPrevDirection = findViewById(R.id.btn_prev_direction);
        btnPrevDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeReader.readPreviousDirection();
            }
        });
        ImageButton btnNextDirection = findViewById(R.id.btn_next_direction);
        btnNextDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeReader.readNextDirection();
            }
        });
    }

    private void toggleListener() {
        if (listening) {
            listenerToggle.setIcon(R.drawable.ic_microphone_off_white_36dp);
            instructionListener.destroy();
            listening = false;
        } else {
            listenerToggle.setIcon(R.drawable.ic_microphone_outline_white_36dp);
            requestAudioPermission();
        }
    }

    private void displayRecipeImage(final Recipe recipe) {
        Picasso.with(this)
                .load(recipe.getImageUrl())
                .fit()
                .into(recipeImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        startPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        startPostponedEnterTransition();
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        displayRecipeImage(recipe);
    }

    private void displayRecipe(final Recipe recipe) {
        StringBuilder ingredientsBuilder = new StringBuilder();
        StringBuilder directionsBuilder = new StringBuilder();

        for (String s : recipe.getIngredients()) {
            ingredientsBuilder.append(s).append("\n");
        }
        for (String s : recipe.getDirections()) {
            directionsBuilder.append(s).append("\n\n");
        }
        int length = directionsBuilder.length();
        directionsBuilder.delete(length - 2, length);
        ingredientsBuilder.deleteCharAt(ingredientsBuilder.length() - 1);
        ingredients.setText(ingredientsBuilder.toString());
        directions.setText(directionsBuilder.toString());
        recipeDetailsLayout.animate().alpha(1.0f).setDuration(1000);
        toolbar.animate().alpha(1.0f).setDuration(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(recipe.getTitle().trim().toUpperCase());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ReadRecipeActivity", "OnResume");
        if (recipeReader == null) {
            requestAudioFocus();
        }
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        displayRecipe(recipe);
    }

    @Override
    protected void onDestroy() {
        Log.i("ReadRecipeActivity", "onDestroy");
        super.onDestroy();
        if (instructionListener != null) {
            instructionListener.destroy();
        }
        recipeReader.destroy();
    }

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (instructionListener != null) {
                        instructionListener.destroy();
                    }
                    recipeReader.destroy();
                    instructionListener = null;
                    recipeReader = null;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    recipeReader.stopReading();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
            }
        }
    };
}
