package com.example.aaron.recipeassistant.Model;


import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {


    private String[] ingredients;
    private String[] directions;
    private String title;
    private String imageUrl;

    public Recipe(String title, String[] ingredients, String[] directions, String imageUrl) {
        this.title = title;
        this.ingredients = ingredients;
        this.directions = directions;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String[] getDirections() {
        return directions;
    }

    public String getImageUrl() {return imageUrl;}

}
