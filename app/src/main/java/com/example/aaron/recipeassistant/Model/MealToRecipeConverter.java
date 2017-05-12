package com.example.aaron.recipeassistant.Model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaron on 5/8/17.
 */

public class MealToRecipeConverter {

    private final Meal meal;
    private List<String> ingredientsList = new ArrayList<>();

    public MealToRecipeConverter(Meal meal) {
        this.meal = meal;
    }

    public Recipe getRecipe() {
        String title = meal.getStrMeal();
        String imageUrl = meal.getStrMealThumb();
        return new Recipe(title, buildIngredients(), buildDirections(), imageUrl);
    }

    private String[] buildIngredients() {
        verifyIngredientStrings(meal.getStrMeasure1(), meal.getStrIngredient1());
        verifyIngredientStrings(meal.getStrMeasure2(), meal.getStrIngredient2());
        verifyIngredientStrings(meal.getStrMeasure3(), meal.getStrIngredient3());
        verifyIngredientStrings(meal.getStrMeasure4(), meal.getStrIngredient4());
        verifyIngredientStrings(meal.getStrMeasure5(), meal.getStrIngredient5());
        verifyIngredientStrings(meal.getStrMeasure6(), meal.getStrIngredient6());
        verifyIngredientStrings(meal.getStrMeasure7(), meal.getStrIngredient7());
        verifyIngredientStrings(meal.getStrMeasure8(), meal.getStrIngredient8());
        verifyIngredientStrings(meal.getStrMeasure9(), meal.getStrIngredient9());
        verifyIngredientStrings(meal.getStrMeasure10(), meal.getStrIngredient10());
        verifyIngredientStrings(meal.getStrMeasure11(), meal.getStrIngredient11());
        verifyIngredientStrings(meal.getStrMeasure12(), meal.getStrIngredient12());
        verifyIngredientStrings(meal.getStrMeasure13(), meal.getStrIngredient13());
        verifyIngredientStrings(meal.getStrMeasure14(), meal.getStrIngredient14());
        verifyIngredientStrings(meal.getStrMeasure15(), meal.getStrIngredient15());
        verifyIngredientStrings(meal.getStrMeasure16(), meal.getStrIngredient16());
        verifyIngredientStrings(meal.getStrMeasure17(), meal.getStrIngredient17());
        verifyIngredientStrings(meal.getStrMeasure18(), meal.getStrIngredient18());
        verifyIngredientStrings(meal.getStrMeasure19(), meal.getStrIngredient19());
        verifyIngredientStrings(meal.getStrMeasure20(), meal.getStrIngredient20());
        String[] ingredientsArray = new String[ingredientsList.size()];
        ingredientsArray = ingredientsList.toArray(ingredientsArray);
        return ingredientsArray;
    }

    private void verifyIngredientStrings(String measurement, String ingredient) {
        if (!TextUtils.isEmpty(measurement) && !measurement.equals("") &&
                !TextUtils.isEmpty(ingredient) && !ingredient.equals("")) {
            ingredientsList.add(measurement + " " + ingredient);
        }
    }

    private String[] buildDirections() {
        return meal.getStrInstructions().split("\\n");
    }
}
