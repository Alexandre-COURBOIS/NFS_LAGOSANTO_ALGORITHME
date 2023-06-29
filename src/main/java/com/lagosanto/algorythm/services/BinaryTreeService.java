package com.lagosanto.algorythm.services;

import com.lagosanto.algorythm.binaryTree.BinaryTree;
import com.lagosanto.algorythm.binaryTree.Node;
import com.lagosanto.algorythm.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BinaryTreeService {
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private WorkUnitService workUnitService;


    public Node buildNode(Recipe recipe, List<Recipe> allRecipes, Map<WorkUnit, Set<Integer>> listWorkUnits) throws IOException {
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

//        Map<Integer, Integer> recipeNode = generateMapForNode(recipe);
//        Map<Integer, Integer> hashMapNodeLeft = generateMapForNode(recipe1);
//        Map<Integer, Integer> hashMapNodeRight = generateMapForNode(recipe2);

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

        Node node = new Node(recipe, null, null, false);

        Node leftNode = buildNode(recipe1, allRecipes, listWorkUnits);
        Node rightNode = buildNode(recipe2, allRecipes, listWorkUnits);

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
        //Initialisation de mon binary Tree avec ma list de workUnits updated avec chaque opération dans ma map de workunit
        return new Tuple(new BinaryTree(buildNode(recipe, listAllRecipe, listWorkUnits)), listWorkUnits);
    }

    public List<Order> treatRecipe(BinaryTree tree, Map<WorkUnit, Set<Integer>> listWorkUnits, int quantity) throws IOException {
        List<Article> articles = articleService.getAllArticles();

        Map<Article, RecipeInfo> mergedArtQty = treeTreatPerLevel(tree, articles, listWorkUnits, quantity);

        return prepareOrder(mergedArtQty, listWorkUnits);
    }

    private List<Order> prepareOrder(Map<Article, RecipeInfo> buildToOrder, Map<WorkUnit, Set<Integer>> workUnitList) {
        List<Order> orderList = createOrderList(workUnitList.keySet());
        List<Order> tempOrderList = new ArrayList<>();

        List<Map.Entry<Article, RecipeInfo>> sortedEntries = buildToOrder.entrySet().stream()
                .sorted(Map.Entry.<Article, RecipeInfo>comparingByValue(
                        Comparator.comparingInt(RecipeInfo::getFirstEncounteredLevel)).reversed())
                .collect(Collectors.toList());

        List<Order> orders = createOrderList(workUnitList.keySet());

        Distributor distributor = new Distributor(workUnitList, sortedEntries);

        Map<WorkUnit, LinkedList<Map.Entry<Article, RecipeInfo>>> recipes = distributor.distributeArticles();

        for (Map.Entry<WorkUnit, LinkedList<Map.Entry<Article, RecipeInfo>>> entry : recipes.entrySet()) {
            WorkUnit workUnit = entry.getKey();
            LinkedList<Map.Entry<Article, RecipeInfo>> articleList = entry.getValue();

            System.out.println("WorkUnit ID: " + workUnit.getCode());

            Optional<Order> optOrd  = orders.stream()
                    .filter(order -> order.getCodeWorkUnit().equals(workUnit.getCode()))
                    .findFirst();

            Order ord = optOrd.get();

            List<OperationOrder> operationOrderList = new ArrayList<>();

            for (Map.Entry<Article, RecipeInfo> articleEntry : articleList) {
                Article article = articleEntry.getKey();
                RecipeInfo recipeInfo = articleEntry.getValue();

                OperationOrder operationOrder = new OperationOrder(article.getCode(),recipeInfo.getQuantity(), operationOrderList.size() + 1) ;
                operationOrderList.add(operationOrder);
            }

            ord.setProductOperations(operationOrderList);
            orderList.add(ord);
        }

        orderList.removeIf(order -> order.getProductOperations().isEmpty());

        return orderList;
    }


    private Map<Article, RecipeInfo> treeTreatPerLevel(BinaryTree tree, List<Article> articles, Map<WorkUnit, Set<Integer>> listWorkUnits, int quantity) throws IOException {
        List<WorkUnit> workUnits = new ArrayList<>();
        LinkedHashMap<Recipe, Integer> articleQty = new LinkedHashMap<>();
        Map<Integer, List<Recipe>> nodesByLevel = new HashMap<>();
        Map<Recipe, RecipeInfo> mapinfo = new HashMap<>();

        BFS(tree.getRoot(), quantity, workUnits, articles, listWorkUnits, articleQty, nodesByLevel);

        return recipesMerge(articles, articleQty, nodesByLevel, mapinfo);
    }

    //Ici j'implémente le Breadth First Search (BFS) Algorithme de parcours horizontal
    private void BFS(Node node, int qty, List<WorkUnit> workUnits, List<Article> articles, Map<WorkUnit, Set<Integer>> listWorkUnits, LinkedHashMap<Recipe, Integer> articleQty, Map<Integer, List<Recipe>> nodesByLevel) throws IOException {
        if (node == null) return;

        Queue<NodeQuantityPair> queue = new LinkedList<>();
        queue.add(new NodeQuantityPair(node, qty, 1)); // niveau initial = 1

        while (!queue.isEmpty()) {
            NodeQuantityPair current = queue.poll();
            Node currentNode = current.getNode();
            int currentQty = current.getQty();
            int currentLevel = current.getLevel();

            if (!nodesByLevel.containsKey(currentLevel)) {
                nodesByLevel.put(currentLevel, new ArrayList<>());
            }
            nodesByLevel.get(currentLevel).add(currentNode.getRecipe());

            articleQty.put(currentNode.getRecipe(), articleQty.getOrDefault(currentNode.getRecipe(), 0) + currentQty);

            if (currentNode.getLeft() != null) {
                queue.add(new NodeQuantityPair(currentNode.getLeft(), currentNode.getRecipe().getQuantite1() * currentQty, currentLevel + 1));
            }

            if (currentNode.getRight() != null) {
                queue.add(new NodeQuantityPair(currentNode.getRight(), currentNode.getRecipe().getQuantite2() * currentQty, currentLevel + 1));
            }
        }
    }

    private Map<Article, RecipeInfo> recipesMerge(List<Article> articleList, LinkedHashMap<Recipe, Integer> articleQty, Map<Integer, List<Recipe>> nodesByLevels, Map<Recipe, RecipeInfo> recipeInfoMap) {
        Map<Article, RecipeInfo> articleAndInfo = new HashMap<>();

        for (Map.Entry<Recipe, Integer> entry : articleQty.entrySet()) {
            Recipe recipe = entry.getKey();
            int quantity = entry.getValue();
            int firstEncounteredLevel = -1; //Si pas trouvé mets la value à -1 (ne doit pas se produire)
            for (Map.Entry<Integer, List<Recipe>> levelEntry : nodesByLevels.entrySet()) {
                if (levelEntry.getValue().contains(recipe)) {
                    firstEncounteredLevel = levelEntry.getKey();
                    break;
                }
            }

            Optional<Article> optArticle = articleList.stream()
                    .filter(art -> art.getId() == recipe.getId_article())
                    .findFirst();

            if (optArticle.isPresent()) {
                articleAndInfo.put(optArticle.get(), new RecipeInfo(quantity, firstEncounteredLevel));
            }
        }

        return articleAndInfo;
    }

    private List<Order> createOrderList(Set<WorkUnit> workUnitList) {
        List<Order> orders = new ArrayList<>();

        for (WorkUnit workunit : workUnitList) {
            orders.add(new Order(workunit.getCode(), new ArrayList<OperationOrder>()));
        }

        return orders;
    }
}
