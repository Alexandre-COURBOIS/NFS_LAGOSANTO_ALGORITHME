package com.lagosanto.algorythm.services;

import com.lagosanto.algorythm.binaryTree.BinaryTree;
import com.lagosanto.algorythm.binaryTree.Node;
import com.lagosanto.algorythm.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class BinaryTreeService {
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private WorkUnitService workUnitService;

    public Node buildNode(Recipe recipe, List<Recipe> allRecipes, Map<Integer, Integer> fk, boolean treated) throws IOException {
        if (recipe == null) {
            return null;
        }

        Optional<Recipe> optRecipe1 = allRecipes.stream()
                .filter(r -> r.getId_article() == recipe.getId_composant1())
                .findFirst();
        Optional<Recipe> optRecipe2 = allRecipes.stream()
                .filter(r -> r.getId_article() == recipe.getId_composant2())
                .findFirst();
        Recipe recipe1 = optRecipe1.orElse(null);
        Recipe recipe2 = optRecipe2.orElse(null);

        Map<Integer, Integer> recipeNode = generateMapForNode(recipe);
        Map<Integer, Integer> hashMapNodeLeft = generateMapForNode(recipe1);
        Map<Integer, Integer> hashMapNodeRight = generateMapForNode(recipe2);

        Node node = new Node(recipe, null, null, false, recipeNode);

        Node leftNode = buildNode(recipe1, allRecipes, hashMapNodeLeft, false);
        Node rightNode = buildNode(recipe2, allRecipes, hashMapNodeRight, false);

        setParent(node, leftNode);
        setParent(node, rightNode);

        node.setLeft(leftNode);
        node.setRight(rightNode);

        return node;
    }

    private void setParent(Node parent, Node child) {
        if (child != null) {
            child.setParent(parent);
        }
    }


    public Tuple map(Recipe recipe) throws IOException {
        List<Recipe> listAllRecipe = recipeService.getAllRecipe();
        Map<WorkUnit, Set<Integer>> listWorkUnits = new HashMap<>();
        Map<Integer, Integer> emptyNodeQty = new HashMap<>();

        BinaryTree binaryTree = new BinaryTree(buildNode(recipe, listAllRecipe, emptyNodeQty, false));

        processTree(binaryTree, listWorkUnits);

        return new Tuple(binaryTree, listWorkUnits);
    }

    public void processTree(BinaryTree binaryTree, Map<WorkUnit, Set<Integer>> listWorkUnits) throws IOException {
        List<Node> leafNodes = binaryTree.getLeafNodes(binaryTree.getRoot(), new ArrayList<>());

        if (leafNodes.isEmpty()) {
            return;
        }

        Map<Recipe, Integer> articleToSubmit = mergeLeafNodeQuantities(leafNodes);

        for (Recipe rcp : articleToSubmit.keySet()) {
            getAvailableWorkUnit(listWorkUnits, rcp);
        }

        processTree(binaryTree, listWorkUnits);
    }

    public Map<Recipe, Integer> mergeLeafNodeQuantities(List<Node> leafNodes) {
        Map<Recipe, Integer> finalMap = new HashMap<>();

        for (Node leafNode : leafNodes) {
            Recipe recipe = leafNode.getRecipe();

            Map<Integer, Integer> objectQuantity = leafNode.getObjectQuantity();
            objectQuantity.entrySet().removeIf(entry -> entry.getKey() == 0 && entry.getValue() == 0);

            int totalQuantity = 0;
            for (Integer quantity : objectQuantity.values()) {
                totalQuantity += quantity;
            }

            if (finalMap.containsKey(recipe)) {
                finalMap.put(recipe, finalMap.get(recipe) + totalQuantity);
            } else {
                finalMap.put(recipe, totalQuantity);
            }
        }
        return finalMap;
    }

    public List<WorkUnit> getAvailableWorkUnit(Map<WorkUnit, Set<Integer>> listWorkUnits, Recipe recipe) throws IOException {
        List<WorkUnit> workUnitAvaiblable = new ArrayList<>();

        for (Map.Entry<WorkUnit, Set<Integer>> entry : listWorkUnits.entrySet()) {
            if (entry.getValue().contains(recipe.getId_operation())) {
                workUnitAvaiblable.add(entry.getKey());
            }
        }

        if (workUnitAvaiblable.size() == 0) {
            workUnitAvaiblable = workUnitService.getWorkUnitsByOperation(recipe.getId_operation());
        }

        for (WorkUnit workUnit : workUnitAvaiblable) {
            if (listWorkUnits.containsKey(workUnit)) {
                listWorkUnits.get(workUnit).add(recipe.getId_operation());
            } else {
                listWorkUnits.put(workUnit, new HashSet<Integer>(recipe.getId_operation()));
            }
        }

        return workUnitAvaiblable;
    }

    public List<Order> prepareOrders(Node node, int qty, Map<WorkUnit, Set<Integer>> listWorkUnits) throws IOException {
        List<WorkUnit> workUnits = new ArrayList<>();
        List<Order> orderList = new ArrayList<>();
        List<Article> articles = articleService.getAllArticles();
        LinkedHashMap<Recipe, Integer> articleQty = new LinkedHashMap<>();

        parcoursPostfixe(node, qty, workUnits, articles, listWorkUnits, articleQty);

        for (Map.Entry<Recipe, Integer> entryArticleQty : articleQty.entrySet()) {
            createOrderForRecipe(workUnits, orderList, articles, listWorkUnits, entryArticleQty);
        }

        return orderList;
    }


    private void parcoursPostfixe(Node node, int qty, List<WorkUnit> workUnits, List<Article> articles, Map<WorkUnit, Set<Integer>> listWorkUnits, LinkedHashMap<Recipe, Integer> articleQty) throws IOException {
        if (node.getLeft() != null) {
            parcoursPostfixe(node.getLeft(), node.getRecipe().getQuantite1() * qty, workUnits, articles, listWorkUnits, articleQty);
        }
        if (node.getRight() != null) {
            parcoursPostfixe(node.getRight(), node.getRecipe().getQuantite2() * qty, workUnits, articles, listWorkUnits, articleQty);
        }
        articleQty.put(node.getRecipe(), articleQty.getOrDefault(node.getRecipe(), 0) + qty);
    }

    private void createOrderForRecipe(List<WorkUnit> workUnits, List<Order> orderList, List<Article> articles, Map<WorkUnit, Set<Integer>> listWorkUnits, Map.Entry<Recipe, Integer> entryArticleQty) {
        for (Map.Entry<WorkUnit, Set<Integer>> entryWorkUnit : listWorkUnits.entrySet()) {
            if (entryWorkUnit.getValue().contains(entryArticleQty.getKey().getId_operation()) && !workUnits.contains(entryWorkUnit.getKey())) {
                workUnits.add(entryWorkUnit.getKey());
                Order order = createOrder(entryWorkUnit, articles, entryArticleQty);
                if (order != null) {
                    orderList.add(order);
                }
                break;
            }
        }
    }

    private Order createOrder(Map.Entry<WorkUnit, Set<Integer>> entryWorkUnit, List<Article> articles, Map.Entry<Recipe, Integer> entryArticleQty) {
        Order order = new Order();
        order.setCodeWorkUnit(entryWorkUnit.getKey().getCode());
        Optional<Article> optArticle = articles.stream()
                .filter(a -> a.getId() == entryArticleQty.getKey().getId_article())
                .findFirst();

        if (optArticle.isPresent()) {
            Article article = optArticle.get();
            List<OperationOrder> operationOrderList = new ArrayList<>();
            OperationOrder operationOrder = new OperationOrder(article.getCode(), entryArticleQty.getValue(), 1);
            operationOrderList.add(operationOrder);
            order.setProductOperations(operationOrderList);
            return order;
        }

        return null;
    }

    private Map<Integer, Integer> generateMapForNode(Recipe recipe) {
        if (recipe == null) {
            return null;
        }
        Map<Integer, Integer> firstRecipeMap = new HashMap<>();
        firstRecipeMap.put(recipe.getId_composant1(), recipe.getQuantite1());
        firstRecipeMap.put(recipe.getId_composant2(), recipe.getQuantite2());
        return firstRecipeMap;
    }
}
