package com.example.aaron.recipeassistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.aaron.recipeassistant.Model.InstructionListener;
import com.example.aaron.recipeassistant.Model.MealList;
import com.example.aaron.recipeassistant.Model.MealService;
import com.example.aaron.recipeassistant.Model.Meal;
import com.example.aaron.recipeassistant.Model.Recipe;
import com.example.aaron.recipeassistant.Model.RecipeReader;
import com.example.aaron.recipeassistant.Model.TestRecipeData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecipeReader recipeReader;

    private InstructionListener instructionListener;

    private TextView ingredients;
    private TextView directions;
    private ImageButton actionListenerToggle;

    private Recipe debugRecipe;
    private boolean listening;

    private AudioManager audioManager;

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        ingredients = (TextView) findViewById(R.id.ingredients_text);
        directions = (TextView) findViewById(R.id.directions_text);

        initButtons();
        initRecipe();
        listening = false;

        MealService recipeService = MealService.retrofit.create(MealService.class);
        Call<MealList> call = recipeService.getRandomSelection();
        call.enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                Log.i("recipeService Callback", response.toString());
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                t.printStackTrace();
            }
        });
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
        recipeReader = new RecipeReader(MainActivity.this);
        recipeReader.setRecipe(debugRecipe);
    }

    private void buildListener() {
        instructionListener = new InstructionListener(MainActivity.this, recipeReader);
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
        debugRecipe = TestRecipeData.createTestRecipe(this);
        displayRecipe(debugRecipe);
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
        actionListenerToggle = (ImageButton) findViewById(R.id.action_listener_toggle);
        actionListenerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listening) {
                    actionListenerToggle.setImageResource(R.drawable.ic_microphone_off_black_36dp);
                    instructionListener.destroy();
                    listening = false;
                } else {
                    actionListenerToggle.setImageResource(R.drawable.ic_microphone_outline_black_36dp);
                    requestAudioPermission();
                }
            }
        });
    }

    private void displayRecipe(Recipe recipe) {
        for (String s : recipe.getIngredients()) {
            ingredients.append(s + "\n");
        }
        for (String s : recipe.getDirections()) {
            directions.append(s + "\n");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MainActivity", "OnStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "OnResume");
        if (recipeReader == null) {
            requestAudioFocus();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("MainActivity", "OnRestart");
    }

    @Override
    protected void onDestroy() {
        Log.i("MainActivity", "onDestroy");
        super.onDestroy();
        if (instructionListener != null) {
            instructionListener.destroy();
        }
        recipeReader.destroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MainActivity", "onStop");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity", "onPause");
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
