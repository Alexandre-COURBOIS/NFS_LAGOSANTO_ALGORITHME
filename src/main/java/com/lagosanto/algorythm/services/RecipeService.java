package com.lagosanto.algorythm.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lagosanto.algorythm.models.Recipe;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    @Value("${project.api.urlbase}")
    private String URLBASE;
    private static final String URL_RECIPES = "recipes/";
    private static final String URL_RECIPE = "recipe/";

    public List<Recipe> getAllRecipe() throws IOException {
        JsonNode json = getJsonNodeFromResponse(URLBASE + URL_RECIPES);
        return parseRecipesFromJson(json);
    }

    public Recipe getRecipe(int idParameter) throws IOException {
        JsonNode json = getJsonNodeFromResponse(URLBASE + URL_RECIPE + idParameter);
        return parseSingleRecipeFromJson(json);
    }

    private JsonNode getJsonNodeFromResponse(String url) throws IOException {
        Connection.Response response = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.body());
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " for url : " + url);
        }
    }

    private List<Recipe> parseRecipesFromJson(JsonNode json) {
        List<Recipe> recipes = new ArrayList<>();
        for (final JsonNode objNode : json) {
            Recipe recipe = createRecipeFromJsonNode(objNode);
            recipes.add(recipe);
        }
        return recipes;
    }

    private Recipe parseSingleRecipeFromJson(JsonNode json) {
        Recipe recipe = null;
        for (final JsonNode objNode : json) {
            recipe = createRecipeFromJsonNode(objNode);
        }
        return recipe;
    }

    private Recipe createRecipeFromJsonNode(JsonNode objNode) {
        int id_article = objNode.get("id_article").asInt();
        int id_operation = objNode.get("id_operation").asInt();
        int id_composant1 = objNode.get("id_composant1").asInt();
        int quantite1 = objNode.get("quantite1").asInt();

        Optional<Integer> opt_id_composant2 = Optional.ofNullable(objNode.get("id_composant2")).map(JsonNode::asInt);
        Optional<Integer> opt_quantite2 = Optional.ofNullable(objNode.get("quantite2")).map(JsonNode::asInt);

        Recipe recipe = new Recipe(id_article,id_operation,id_composant1,quantite1);
        opt_id_composant2.ifPresent(recipe::setId_composant2);
        opt_quantite2.ifPresent(recipe::setQuantite2);

        return recipe;
    }
}

