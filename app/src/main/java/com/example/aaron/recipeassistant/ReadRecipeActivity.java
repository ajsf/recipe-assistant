package com.example.aaron.recipeassistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aaron.recipeassistant.Model.InstructionListener;
import com.example.aaron.recipeassistant.Model.Recipe;
import com.example.aaron.recipeassistant.Model.RecipeReader;
import com.example.aaron.recipeassistant.Model.TestRecipeData;
import com.squareup.picasso.Picasso;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

public class ReadRecipeActivity extends AppCompatActivity {

    private RecipeReader recipeReader;

    private InstructionListener instructionListener;

    private TextView ingredients;
    private TextView directions;

    private ImageView recipeImage;

    private Recipe recipe;
    private boolean listening;

    private AudioManager audioManager;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private MenuItem listenerToggle;

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_recipe);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        ingredients = (TextView) findViewById(R.id.ingredients_text);
        directions = (TextView) findViewById(R.id.directions_text);

        recipeImage = (ImageView) findViewById(R.id.recipe_header_image);

        recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        initButtons();
        initRecipe();
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

    private void initRecipe() {
        if (recipe == null) {
            recipe = TestRecipeData.createTestRecipe(this);
        }
        displayRecipe(recipe);
    }

    private void initButtons() {
        Button actionListIngredients = (Button) findViewById(R.id.action_list_ingredients);
        actionListIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeReader.readIngredients();
            }
        });
        Button actionPreviousDirection = (Button) findViewById(R.id.action_prev_direction);
        actionPreviousDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeReader.readPreviousDirection();
            }
        });
        Button actionNextDirection = (Button) findViewById(R.id.action_next_direction);
        actionNextDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeReader.readNextDirection();
            }
        });
    }

    private void toggleListener() {
        if (listening) {
            listenerToggle.setIcon(R.drawable.ic_microphone_off_white_36dp);
            //actionListenerToggle.setImageResource(R.drawable.ic_microphone_off_black_36dp);
            instructionListener.destroy();
            listening = false;
        } else {
            listenerToggle.setIcon(R.drawable.ic_microphone_outline_white_36dp);
            //actionListenerToggle.setImageResource(R.drawable.ic_microphone_outline_black_36dp);
            requestAudioPermission();
        }
    }

    private void displayRecipe(Recipe recipe) {
        collapsingToolbarLayout.setTitle(recipe.getTitle().trim().toUpperCase());

        Picasso.with(this).load(recipe.getImageUrl())
                .into(recipeImage);

        StringBuilder ingredientsBuilder = new StringBuilder();
        StringBuilder directionsBuilder = new StringBuilder();

        for (String s : recipe.getIngredients()) {
            ingredientsBuilder.append(s + "\n");
        }
        for (String s : recipe.getDirections()) {
            directionsBuilder.append(s + "\n\n");
        }
        int length = directionsBuilder.length();
        directionsBuilder.delete(length - 2, length);
        ingredientsBuilder.deleteCharAt(ingredientsBuilder.length() - 1);
        ingredients.setText(ingredientsBuilder.toString());
        directions.setText(directionsBuilder.toString());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("ReadRecipeActivity", "OnStart");

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
    protected void onRestart() {
        super.onRestart();
        Log.i("ReadRecipeActivity", "OnRestart");
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

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("ReadRecipeActivity", "onStop");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("ReadRecipeActivity", "onPause");
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
                    //buildReaderAndListener();
                    break;
            }
        }
    };
}
