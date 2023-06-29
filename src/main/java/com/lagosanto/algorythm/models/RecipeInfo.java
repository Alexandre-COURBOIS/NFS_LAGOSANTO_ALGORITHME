package com.lagosanto.algorythm.models;

public class RecipeInfo {
    private int quantity;
    private int firstEncounteredLevel;

    public RecipeInfo(int quantity, int firstEncounteredLevel) {
        this.quantity = quantity;
        this.firstEncounteredLevel = firstEncounteredLevel;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getFirstEncounteredLevel() {
        return firstEncounteredLevel;
    }

    public void setFirstEncounteredLevel(int firstEncounteredLevel) {
        this.firstEncounteredLevel = firstEncounteredLevel;
    }
}