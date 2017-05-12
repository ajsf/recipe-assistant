package com.example.aaron.recipeassistant.Model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class InstructionListener implements RecognitionListener {

    private static final String INSTRUCTION_SEARCH = "instruction";

    private static final String INGREDIENTS = "INGREDIENTS";
    private static final String PREV_DIRECTION = "PREVIOUS DIRECTION";
    private static final String NEXT_DIRECTION = "NEXT DIRECTION";
    private static final String FIRST_DIRECTION = "FIRST DIRECTION";
    private static final String FINAL_DIRECTION = "FINAL DIRECTION";

    private static final List<String> INSTRUCTIONS_LIST = Arrays.asList(INGREDIENTS, PREV_DIRECTION, NEXT_DIRECTION,
            FIRST_DIRECTION, FINAL_DIRECTION);


    private SpeechRecognizer recognizer;
    private Context context;
    private RecipeReader recipeReader;

    public InstructionListener(Context context, RecipeReader recipeReader) {
        this.recipeReader = recipeReader;
        this.context = context;
        initRecognizer();
    }

    private void initRecognizer() {
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(context);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Exception e) {
                if (e != null) {
                    Toast.makeText(context, "Failed to init recognizer " + e, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Recognizer initialized", Toast.LENGTH_LONG).show();

                }
            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "5660.dic"))
                .getRecognizer();

        recognizer.addListener(this);
        //File languageModel = new File(assetsDir, "5660.lm");
        //recognizer.addNgramSearch(INSTRUCTION_SEARCH, languageModel);
        File instructionsGrammer = new File(assetsDir, "5660.gram");
        recognizer.addKeywordSearch(INSTRUCTION_SEARCH, instructionsGrammer);

        recognizer.startListening(INSTRUCTION_SEARCH);
    }

    public void destroy() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i("stt", "beginning of speech");

    }

    @Override
    public void onEndOfSpeech() {
        //recognizer.stop();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {

        Log.i("stt", "partial result");
        if (hypothesis != null) {
            recognizer.stop();
            String text = hypothesis.getHypstr();
            Log.i("stt", "onPartialResult: " + text);
            recipeReader.stopReading();
            processInstruction(text);
            recognizer.startListening(INSTRUCTION_SEARCH);
        }
    }

    private void processInstruction(String instruction) {
        switch (instruction) {
            case INGREDIENTS:
                recipeReader.readIngredients();
                break;
            case NEXT_DIRECTION:
                recipeReader.readNextDirection();
                break;
            case PREV_DIRECTION:
                recipeReader.readPreviousDirection();
                break;
            case FIRST_DIRECTION:
                recipeReader.readFirstDirection();
                break;
            case FINAL_DIRECTION:
                recipeReader.readLastDirection();
                break;
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {

    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeout() {
    }

}
