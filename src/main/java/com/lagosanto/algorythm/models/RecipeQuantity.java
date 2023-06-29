package com.lagosanto.algorythm.models;

public class RecipeQuantity {
    private Recipe recipe;
    private Integer quantity;

    public RecipeQuantity(Recipe recipe, Integer quantity) {
        this.recipe = recipe;
        this.quantity = quantity;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}