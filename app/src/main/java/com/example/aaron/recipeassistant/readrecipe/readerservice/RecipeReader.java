package com.example.aaron.recipeassistant.readrecipe.readerservice;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import com.example.aaron.recipeassistant.model.Recipe;

import java.util.Locale;
import java.util.Set;

public class RecipeReader {

    //private Context context;
    private TextToSpeech tts;
    private Recipe recipe;

    private int currentDirection = -1;
    private int totalDirections;


    public RecipeReader(Context context) {
        //this.context = context;
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This language is not supported");
                    }
                    Set<Voice> voices = tts.getVoices();
                    String voiceName = "en-us-x-sfg#female_2-local";
                    for (Voice tmpVoice : voices) {
                        if (tmpVoice.getName().equals(voiceName)) {
                            tts.setVoice(tmpVoice);
                        }
                    }
                } else {
                    Log.e("TTS", "Initialization Failed!");
                }
            }
        });

    }

    private void speak(final String text, final int queueMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, queueMode, null, null);
        } else {
            tts.speak(text, queueMode, null);
        }
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        totalDirections = recipe.getDirections().length;
    }

    public void readIngredients() {
        for (String ingredient : recipe.getIngredients()) {
            speak(ingredient, TextToSpeech.QUEUE_ADD);
        }
    }

    private void readDirection() {
        speak(recipe.getDirections()[currentDirection], TextToSpeech.QUEUE_FLUSH);
    }

    public void readFirstDirection() {
        currentDirection = 0;
        readDirection();
    }

    public void readLastDirection() {
        currentDirection = totalDirections - 1;
        readDirection();
    }

    public void readPreviousDirection() {
        currentDirection--;
        if (currentDirection < 0) {
            currentDirection = 0;
        }
        readDirection();
    }

    public void readNextDirection() {
        currentDirection++;
        if (currentDirection >= totalDirections) {
            currentDirection = totalDirections - 1;
        }
        readDirection();
    }

    public void stopReading() {
        tts.stop();
    }

    public void destroy() {
        stopReading();
        tts.shutdown();
    }
}
