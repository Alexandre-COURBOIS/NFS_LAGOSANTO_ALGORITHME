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

    public Node constructNode(Recipe recipe, List<Recipe> allRecipes, Map<WorkUnit, Set<Integer>> listWorkUnits) throws IOException {
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


        List<WorkUnit> workUnitAvaiblable = new ArrayList<>();
        for (Map.Entry<WorkUnit, Set<Integer>> entry : listWorkUnits.entrySet()) {
            //Si la workunit contient l'id de l'opération nécessaire je l'ajoute à ma liste de workunit disponibles
            if (entry.getValue().contains(recipe.getId_operation())) {
                workUnitAvaiblable.add(entry.getKey());
            }
        }

        //Si je n'ai pas de workunit disponible je fais une requête afin de récupérer une workunit qui l'est en fonction de l'idOpération
        if (workUnitAvaiblable.size() == 0) {
            workUnitAvaiblable = workUnitService.getWorkUnitsByOperation(recipe.getId_operation());
        }


        //Itération sur mes machines disponibles
        for (WorkUnit workUnit : workUnitAvaiblable) {
            //Si dans ma map de workunit j'ai bien la workunit actuelle
            if (listWorkUnits.containsKey(workUnit)) {
                //Je recupère la workunit en cours, et j'ajoute l'id de l'opération à cette workunit
                listWorkUnits.get(workUnit).add(recipe.getId_operation());
            } else {
                //dans ma map de workunit j'ajoute la workunit actuelle avec l'idOpération de la recette
                listWorkUnits.put(workUnit, new HashSet<Integer>(recipe.getId_operation()));
            }
        }

        //Génération des noeuds de mon arbre binaire
        return new Node(recipe, constructNode(recipe1, allRecipes, listWorkUnits), constructNode(recipe2, allRecipes, listWorkUnits));
    }

    public Tuple map(Recipe recipe) throws IOException {
        List<Recipe> listAllRecipe = recipeService.getAllRecipe();
        Map<WorkUnit, Set<Integer>> listWorkUnits = new HashMap<>();

        //Initialisation de mon binary Tree avec ma list de workUnits updated avec chaque opérations dans ma map de workunit
        return new Tuple(new BinaryTree(constructNode(recipe, listAllRecipe, listWorkUnits)), listWorkUnits);
    }

    public List<Order> prepareOrders(Node node, int qty, Map<WorkUnit, Set<Integer>> listWorkUnits) throws IOException {
        List<WorkUnit> workUnits = new ArrayList<>();
        List<Order> orderList = new ArrayList<>();
        List<Article> articles = articleService.getAllArticles();
        LinkedHashMap<Recipe, Integer> articleQty = new LinkedHashMap<>();

        postOrderTraversalToUpdateQuantity(node, qty, workUnits, articles, listWorkUnits, articleQty);

        for (Map.Entry<Recipe, Integer> entryArticleQty : articleQty.entrySet()) {
            createOrderForRecipe(workUnits, orderList, articles, listWorkUnits, entryArticleQty);
        }

        return orderList;
    }

    private void postOrderTraversalToUpdateQuantity(Node node, int qty, List<WorkUnit> workUnits, List<Article> articles, Map<WorkUnit, Set<Integer>> listWorkUnits, LinkedHashMap<Recipe, Integer> articleQty) throws IOException {
        if (node.getLeft() != null) {
            postOrderTraversalToUpdateQuantity(node.getLeft(), node.getRecipe().getQuantite1() * qty, workUnits, articles, listWorkUnits, articleQty);
        }
        if (node.getRight() != null) {
            postOrderTraversalToUpdateQuantity(node.getRight(), node.getRecipe().getQuantite2() * qty, workUnits, articles, listWorkUnits, articleQty);
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

    private Order createOrder(Map.Entry<WorkUnit, Set<Integer>> entryWorkUnit, List<Article> articles, Map.Entry<Recipe, Integer> articleQuantity) {
        Order order = new Order();
        order.setCodeWorkUnit(entryWorkUnit.getKey().getCode());
        Optional<Article> isArticle = articles.stream()
                .filter(a -> a.getId() == articleQuantity.getKey().getId_article())
                .findFirst();

        if (isArticle.isPresent()) {
            Article article = isArticle.get();
            List<OperationOrder> operationOrderList = new ArrayList<>();
            OperationOrder operationOrder = new OperationOrder(article.getCode(), articleQuantity.getValue(), 1);
            operationOrderList.add(operationOrder);
            order.setProductOperations(operationOrderList);
            return order;
        }

        return null;
    }

}
