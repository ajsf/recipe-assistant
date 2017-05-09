package com.example.aaron.recipeassistant.Model;

import android.content.Context;

import com.example.aaron.recipeassistant.R;

public class TestRecipeData {


    public static final Recipe createTestRecipe(Context context) {
        String[] ingredients = extractLinesFromString(context.getString(R.string.debug_ingredient_string));
        String[] directions = extractLinesFromString(context.getString(R.string.debug_directionsString));
        return new Recipe(ingredients, directions, new Meal[5]);
    }

    private static String[] extractLinesFromString(String string) {
        String[] strings = string.split("\\r?\\n");
        String[] result = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = strings[i].trim();
        }
        return result;
    }
}
