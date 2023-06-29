package com.lagosanto.algorythm.models;

import java.util.*;

public class Distributor {

    private Map<WorkUnit, Set<Integer>> workUnitToOperations;
    private List<Map.Entry<Article, RecipeInfo>> articlesWithRecipeInfo;

    public Distributor(Map<WorkUnit, Set<Integer>> workUnitToOperations, List<Map.Entry<Article, RecipeInfo>> articlesWithRecipeInfo) {
        this.workUnitToOperations = workUnitToOperations;
        this.articlesWithRecipeInfo = articlesWithRecipeInfo;
    }

    public Map<WorkUnit, LinkedList<Map.Entry<Article, RecipeInfo>>> distributeArticles() {
        Map<WorkUnit, LinkedList<Map.Entry<Article, RecipeInfo>>> workUnitToArticles = new HashMap<>();

        //Initialiser la liste d'articles pour chaque WorkUnit
        for (WorkUnit workUnit : workUnitToOperations.keySet()) {
            workUnitToArticles.put(workUnit, new LinkedList<>());
        }

        for (Map.Entry<Article, RecipeInfo> articleEntry : articlesWithRecipeInfo) {
            Article article = articleEntry.getKey();
            int operationId = article.getId_categorie();

            List<WorkUnit> eligibleWorkUnits = new ArrayList<>();
            for (Map.Entry<WorkUnit, Set<Integer>> entry : workUnitToOperations.entrySet()) {
                if (entry.getValue().contains(operationId)) {
                    eligibleWorkUnits.add(entry.getKey());
                }
            }

            if (!eligibleWorkUnits.isEmpty()) {
                //Répartir les articles de manière équitable entre les workunits
                WorkUnit selectedWorkUnit = eligibleWorkUnits.get(article.getId() % eligibleWorkUnits.size());
                workUnitToArticles.get(selectedWorkUnit).add(articleEntry);
            }
        }

        return workUnitToArticles;
    }
}


