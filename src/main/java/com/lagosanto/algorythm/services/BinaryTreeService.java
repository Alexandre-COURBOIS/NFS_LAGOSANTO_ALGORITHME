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

        return new Node(recipe, constructNode(recipe1, allRecipes, listWorkUnits), constructNode(recipe2, allRecipes, listWorkUnits));
    }

    public Tuple map(Recipe recipe, List<Recipe> listAllRecipe) throws IOException {
        Map<WorkUnit, Set<Integer>> listWorkUnits = new HashMap<>();

        return new Tuple(new BinaryTree(constructNode(recipe, listAllRecipe, listWorkUnits)), listWorkUnits);
    }

    public List<Order> prepareOrders(Node node, int qty, Map<WorkUnit, Set<Integer>> listWorkUnits, List<Recipe> allRecipes) throws IOException {
        List<WorkUnit> workUnits = new ArrayList<>();
        List<Article> articles = articleService.getAllArticles();
        List<RecipeQuantity> recipeQuantities = new ArrayList<>();
        List<WorkunitOperation> workunitOperationList = new ArrayList<>();


        postFixTreatment(node, qty, workUnits, articles, listWorkUnits, recipeQuantities);

        for (Map.Entry<WorkUnit, Set<Integer>> entry : listWorkUnits.entrySet()) {
            WorkUnit workUnit = entry.getKey();
            List<Integer> operations = new ArrayList<>(entry.getValue());
            WorkunitOperation workunitOperation = new WorkunitOperation(workUnit, operations);
            workunitOperationList.add(workunitOperation);
        }
        List<ArticleQuantity> articleQuantityList = buildArticleList(recipeQuantities, articles);

        List<Order> orderList = createOrderList(workunitOperationList);

        dispatchArticleToWorkUnit(articleQuantityList,allRecipes, workunitOperationList, orderList);

        return orderList;
    }

    private List<Order> dispatchArticleToWorkUnit(List<ArticleQuantity> articleQuantityList,List<Recipe> recipes, List<WorkunitOperation> listWorkUnitOperation, List<Order> orderList) {

        for (ArticleQuantity articleQuantity : articleQuantityList) {
            int operationIdArticle = articleQuantity.getArticle().getId();

            Optional<Recipe> optRecipe = recipes.stream()
                    .filter(recipe -> recipe.getId_article() == operationIdArticle)
                    .findFirst();

            if (optRecipe.isPresent()) {
                Recipe recipe = optRecipe.get();

                for (WorkunitOperation workunitOperation : listWorkUnitOperation) {

                    if (workunitOperation.getOperations().contains(recipe.getId_operation())) {
                        Optional<Order> optionalWorkunitOperation = orderList.stream()
                                .filter(order -> order.getCodeWorkUnit().equals(workunitOperation.getWorkUnit().getCode()))
                                .findFirst();

                        if (optionalWorkunitOperation.isPresent()) {
                            Order currentOrder = optionalWorkunitOperation.get();

                            Optional<OperationOrder> existingOperationOrderOptional = currentOrder.getProductOperations().stream()
                                    .filter(operationOrder -> operationOrder.getCodeArticle().equals(articleQuantity.getArticle().getCode()))
                                    .findFirst();

                            if (existingOperationOrderOptional.isPresent()) {
                                OperationOrder existingOperationOrder = existingOperationOrderOptional.get();
                                existingOperationOrder.setProductQuantity(existingOperationOrder.getProductQuantity() + articleQuantity.getQuantity());
                            } else {
                                OperationOrder newOperationOrder = new OperationOrder(articleQuantity.getArticle().getCode(), articleQuantity.getQuantity(), currentOrder.getProductOperations().size() == 0 ? 1 : currentOrder.getProductOperations().size() + 1);
                                currentOrder.getProductOperations().add(newOperationOrder);
                            }

                            break;
                        }
                    }
                }
            }
        }
        return orderList;
    }

    private List<ArticleQuantity> buildArticleList(List<RecipeQuantity> recipeQuantities, List<Article> articleList) {

        List<ArticleQuantity> articleQuantity = new ArrayList<>();

        for (RecipeQuantity recipeqty : recipeQuantities) {

            Optional<Article> optArticle = articleList.stream()
                    .filter(a -> a.getId() == recipeqty.getRecipe().getId_article())
                    .findFirst();

            if (optArticle.isPresent()) {
                ArticleQuantity artQuant = new ArticleQuantity(optArticle.get(), recipeqty.getQuantity());
                articleQuantity.add(artQuant);
            }
        }

        return articleQuantity;
    }


    private void postFixTreatment(Node node, int qty, List<WorkUnit> workUnits, List<Article> articles, Map<WorkUnit, Set<Integer>> listWorkUnits, List<RecipeQuantity> recipeQuantities) throws IOException {
        if (node.getLeft() != null) {
            postFixTreatment(node.getLeft(), node.getRecipe().getQuantite1() * qty, workUnits, articles, listWorkUnits, recipeQuantities);
        }
        if (node.getRight() != null) {
            postFixTreatment(node.getRight(), node.getRecipe().getQuantite2() * qty, workUnits, articles, listWorkUnits, recipeQuantities);
        }
        recipeQuantities.add(new RecipeQuantity(node.getRecipe(), qty));
    }

    private List<Order> createOrderList(List<WorkunitOperation> workUnitList) {
        List<Order> orders = new ArrayList<>();

        for (WorkunitOperation workunit : workUnitList) {
            orders.add(new Order(workunit.getWorkUnit().getCode(), new ArrayList<OperationOrder>()));
        }

        return orders;
    }

}
