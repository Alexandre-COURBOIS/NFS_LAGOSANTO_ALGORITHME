package com.lagosanto.algorythm.models;

import com.lagosanto.algorythm.binaryTree.Node;
import lombok.Data;

@Data
public class NodeQuantityPair {
    private Node node;
    private int qty;
    private int level;

    public NodeQuantityPair(Node node, int qty, int level) {
        this.node = node;
        this.qty = qty;
        this.level = level;
    }

    public Node getNode() {
        return node;
    }

    public int getQty() {
        return qty;
    }

    public int getLevel() {
        return level;
    }
}

