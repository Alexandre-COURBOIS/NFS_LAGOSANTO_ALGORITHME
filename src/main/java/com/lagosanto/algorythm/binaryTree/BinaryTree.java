package com.lagosanto.algorythm.binaryTree;

import java.util.List;

public class BinaryTree {
    private Node root;

    public BinaryTree(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public List<Node> getLeafNodes(Node node, List<Node> leafNodes) {
        if (node == null || node.isTreated())
            return leafNodes;

        if (node.getLeft() == null && node.getRight() == null) {
            leafNodes.add(node);
            node.setTreated(true);

            Node parent = node.getParent();
            while (parent != null) {
                if (parent.getLeft() != null && !parent.getLeft().isTreated()) break;
                if (parent.getRight() != null && !parent.getRight().isTreated()) break;

                parent.setTreated(true);
                parent = parent.getParent();
            }

            return leafNodes;
        }

        getLeafNodes(node.getLeft(), leafNodes);
        getLeafNodes(node.getRight(), leafNodes);

        return leafNodes;
    }


    public void printInOrder(Node node) {
        if (node != null) {
            printInOrder(node.getLeft());  // Visit left child
            System.out.println(node.getRecipe().toString());  // Print data of node
            printInOrder(node.getRight()); // Visit right child
        }
    }

}
