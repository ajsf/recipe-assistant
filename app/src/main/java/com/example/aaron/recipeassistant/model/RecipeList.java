package com.example.aaron.recipeassistant.model;

import java.util.List;

public class RecipeList {

    private List<RecipeDTO> meals = null;

    public List<RecipeDTO> getMeals() {
        return meals;
    }

    public void setMeals(List<RecipeDTO> meals) {
        this.meals = meals;
    }

}
