package com.lagosanto.algorythm.binaryTree;

import com.lagosanto.algorythm.models.Recipe;

public class Node {
    private Node left;
    private Node right;

    public Recipe recipe;

    public Node(Recipe recipe, Node left, Node right) {
        this.left = left;
        this.right = right;
        this.recipe = recipe;
    }

    public Node(Recipe recipe) {
        this.recipe = recipe;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe value) {
        this.recipe = value;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }
}