package com.lagosanto.algorythm.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lagosanto.algorythm.models.Category;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Value("${project.api.urlbase}")
    private String URLBASE;
    private static final String URL_CATEGORIES = "categories/";
    private static final String URL_CATEGORY = "category/";

    public List<Category> getAllCategories() throws IOException {
        JsonNode json = getJsonNodeFromResponse(URLBASE + URL_CATEGORIES);
        return parseCategoriesFromJson(json);
    }

    public Category getCategory(int idParameter) throws IOException {
        JsonNode json = getJsonNodeFromResponse(URLBASE + URL_CATEGORY + idParameter);
        return parseSingleCategoryFromJson(json);
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

    private List<Category> parseCategoriesFromJson(JsonNode json) {
        List<Category> categories = new ArrayList<>();
        for (final JsonNode objNode : json) {
            Category category = createCategoryFromJsonNode(objNode);
            categories.add(category);
        }
        return categories;
    }

    private Category parseSingleCategoryFromJson(JsonNode json) {
        Category category = null;
        for (final JsonNode objNode : json) {
            category = createCategoryFromJsonNode(objNode);
        }
        return category;
    }

    private Category createCategoryFromJsonNode(JsonNode objNode) {
        int id = objNode.get("id").asInt();
        String code = objNode.get("code").asText();
        String libelle = objNode.get("libelle").asText();
        return new Category(id, code, libelle);
    }
}

