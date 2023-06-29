package com.lagosanto.algorythm.models;

import com.lagosanto.algorythm.binaryTree.BinaryTree;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class Tuple {
    BinaryTree tree;
    Map<WorkUnit, Set<Integer>> listWorkUnits;

    public Tuple(BinaryTree tree, Map<WorkUnit, Set<Integer>> listWorkUnits) {
        this.tree = tree;
        this.listWorkUnits = listWorkUnits;
    }
}
