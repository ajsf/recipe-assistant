package com.example.aaron.recipeassistant.Model;


public class Recipe {

    private Meal[] meals;

    private String[] ingredients;
    private String[] directions;
    private String title;

    public Recipe(String[] ingredients, String[] directions, Meal[] meals) {
        this.meals = meals;
        this.ingredients = ingredients;
        this.directions = directions;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String[] getDirections() {
        return directions;
    }
}
