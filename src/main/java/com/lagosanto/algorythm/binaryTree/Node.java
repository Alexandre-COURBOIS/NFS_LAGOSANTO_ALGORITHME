package com.lagosanto.algorythm.binaryTree;

import com.lagosanto.algorythm.models.Recipe;

import java.util.Map;

public class Node {
    private Node left;
    private Node right;
    private Map<Integer, Integer> objectQuantity;
    private Node parent;
    boolean treated;
    public Recipe recipe;

    public Node(Recipe recipe, Node left, Node right,Boolean treated, Map<Integer, Integer> objectQuantity) {
        this.left = left;
        this.right = right;
        this.recipe = recipe;
        this.treated = treated;
        this.objectQuantity = objectQuantity;
    }

    public Map<Integer, Integer> getObjectQuantity() {
        return objectQuantity;
    }

    public void setObjectQuantity(Map<Integer, Integer> objectQuantity) {
        this.objectQuantity = objectQuantity;
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

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isTreated() {
        return treated;
    }

    public void setTreated(boolean treated) {
        this.treated = treated;
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
