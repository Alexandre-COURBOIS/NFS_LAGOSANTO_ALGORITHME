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
    private ArticleService articleService;
    @Autowired
    private WorkUnitService workUnitService;

    public Node constructNode(Recipe recipe, List<Recipe> allRecipes, Map<WorkUnit, Set<Integer>> listWorkUnits) throws IOException {
        if (recipe == null) {
            return null;
        }

        Recipe recipe1 = findRecipeById(recipe.getId_composant1(), allRecipes);
        Recipe recipe2 = findRecipeById(recipe.getId_composant2(), allRecipes);

        List<WorkUnit> workUnitAvaiblable = getWorkUnitsAvailable(listWorkUnits, recipe);

        updateWorkUnits(listWorkUnits, workUnitAvaiblable, recipe);

        return new Node(recipe, constructNode(recipe1, allRecipes, listWorkUnits), constructNode(recipe2, allRecipes, listWorkUnits));
    }

    private Recipe findRecipeById(int id, List<Recipe> allRecipes) {
        return allRecipes.stream()
                .filter(r -> r.getId_article() == id)
                .findFirst()
                .orElse(null);
    }

    private List<WorkUnit> getWorkUnitsAvailable(Map<WorkUnit, Set<Integer>> listWorkUnits, Recipe recipe) throws IOException {
        List<WorkUnit> workUnitAvaiblable = new ArrayList<>();
        for (Map.Entry<WorkUnit, Set<Integer>> entry : listWorkUnits.entrySet()) {
            if (entry.getValue().contains(recipe.getId_operation())) {
                workUnitAvaiblable.add(entry.getKey());
            }
        }
        if (workUnitAvaiblable.isEmpty()) {
            workUnitAvaiblable = workUnitService.getWorkUnitsByOperation(recipe.getId_operation());
        }
        return workUnitAvaiblable;
    }

    private void updateWorkUnits(Map<WorkUnit, Set<Integer>> listWorkUnits, List<WorkUnit> workUnitAvaiblable, Recipe recipe) {
        for (WorkUnit workUnit : workUnitAvaiblable) {
            listWorkUnits.computeIfAbsent(workUnit, k -> new HashSet<>()).add(recipe.getId_operation());
        }
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
        List<ArticleQuantity> articleQuantityList = new ArrayList<>();

        postFixTreatment(node, qty, workUnits, articles, listWorkUnits, recipeQuantities);
        createWorkUnitOperationList(listWorkUnits, workunitOperationList);
        createArticleQuantityList(articleQuantityList, recipeQuantities, articles);
        List<Order> orderList = createOrderList(workunitOperationList);
        dispatchArticleToWorkUnit(articleQuantityList, allRecipes, workunitOperationList, orderList);

        return orderList;
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

    private void dispatchArticleToWorkUnit(List<ArticleQuantity> articleQuantityList, List<Recipe> recipes, List<WorkunitOperation> listWorkUnitOperation, List<Order> orderList) {
        for (ArticleQuantity articleQuantity : articleQuantityList) {
            recipes.stream()
                    .filter(recipe -> recipe.getId_article() == articleQuantity.getArticle().getId())
                    .findFirst()
                    .ifPresent(recipe -> processRecipe(articleQuantity, listWorkUnitOperation, orderList, recipe));
        }
    }

    private void processRecipe(ArticleQuantity articleQuantity, List<WorkunitOperation> listWorkUnitOperation, List<Order> orderList, Recipe recipe) {
        for (WorkunitOperation workunitOperation : listWorkUnitOperation) {
            if (workunitOperation.getOperations().contains(recipe.getId_operation())) {

                orderList.stream()
                        .filter(order -> order.getCodeWorkUnit().equals(workunitOperation.getWorkUnit().getCode()))
                        .findFirst()
                        .ifPresent(order -> generateOrderList(articleQuantity, order));
                break;
            }
        }
    }

    private void generateOrderList(ArticleQuantity articleQuantity, Order currentOrder) {
        currentOrder.getProductOperations().stream()
                .filter(operationOrder -> operationOrder.getCodeArticle().equals(articleQuantity.getArticle().getCode()))
                .findFirst()
                .ifPresentOrElse(existingOperationOrder -> existingOperationOrder.setProductQuantity(existingOperationOrder.getProductQuantity() + articleQuantity.getQuantity()),
                        () -> {
                            OperationOrder newOperationOrder = new OperationOrder(articleQuantity.getArticle().getCode(), articleQuantity.getQuantity(), currentOrder.getProductOperations().size() == 0 ? 1 : currentOrder.getProductOperations().size() + 1);
                            currentOrder.getProductOperations().add(newOperationOrder);
                        });
    }

    private void createArticleQuantityList(List<ArticleQuantity> articleQuantityList, List<RecipeQuantity> recipeQuantities, List<Article> articleList) {
        for (RecipeQuantity recipeqty : recipeQuantities) {
            articleList.stream()
                    .filter(a -> a.getId() == recipeqty.getRecipe().getId_article())
                    .findFirst()
                    .ifPresent(article -> articleQuantityList.add(new ArticleQuantity(article, recipeqty.getQuantity())));
        }
    }

    private void createWorkUnitOperationList(Map<WorkUnit, Set<Integer>> listWorkUnits, List<WorkunitOperation> workunitOperationList) {
        for (Map.Entry<WorkUnit, Set<Integer>> entry : listWorkUnits.entrySet()) {
            WorkUnit workUnit = entry.getKey();
            List<Integer> operations = new ArrayList<>(entry.getValue());
            workunitOperationList.add(new WorkunitOperation(workUnit, operations));
        }
    }

    private List<Order> createOrderList(List<WorkunitOperation> workUnitList) {
        List<Order> orders = new ArrayList<>();
        for (WorkunitOperation workunit : workUnitList) {
            orders.add(new Order(workunit.getWorkUnit().getCode(), new ArrayList<>()));
        }
        return orders;
    }
}
