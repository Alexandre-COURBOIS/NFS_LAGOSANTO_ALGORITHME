package com.lagosanto.algorythm.binaryTree;

import com.lagosanto.algorythm.models.Recipe;

public class NodeLevelPair {
    private Recipe recipe;
    private int level;

    public NodeLevelPair(Recipe recipe, int level) {
        this.recipe = recipe;
        this.level = level;
    }

    public Recipe getNode() {
        return recipe;
    }

    public int getLevel() {
        return level;
    }
}