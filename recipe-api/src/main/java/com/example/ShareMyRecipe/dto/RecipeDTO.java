package com.example.ShareMyRecipe.dto;

import com.example.ShareMyRecipe.entity.Recipe;

import java.time.LocalDateTime;

public class RecipeDTO {
    private String title;
    private String summary;
    private String ingredients;
    private String steps;
    private Long chefId;
    public RecipeDTO(){

    }

    public RecipeDTO(Recipe recipe) {
        this.title = recipe.getTitle();
        this.summary = recipe.getSummary();
        this.ingredients = recipe.getIngredients();
        this.steps = recipe.getSteps();
        this.chefId = recipe.getChef() != null ? recipe.getChef().getId() : null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public Long getChefId() {
        return chefId;
    }

    public void setChefId(Long chefId) {
        this.chefId = chefId;
    }
}
