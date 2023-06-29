package com.lagosanto.algorythm.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lagosanto.algorythm.models.Article;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {

    @Value("${project.api.urlbase}")
    private String URLBASE;
    private static final String ALL_ARTICLES = "articles/";
    private static final String ARTICLE = "article/";

    public List<Article> getAllArticles() throws IOException {
        JsonNode json = getJsonNodeFromResponse(URLBASE + ALL_ARTICLES);
        return parseArticlesFromJson(json);
    }

    public Article getArticle(int idParameter) throws IOException {
        JsonNode json = getJsonNodeFromResponse(URLBASE + ARTICLE + idParameter);
        return parseSingleArticleFromJson(json);
    }

    private JsonNode getJsonNodeFromResponse(String url) throws IOException {
        Response response = Jsoup.connect(url)
                .method(Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.body());
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " for url : " + url);
        }
    }

    private List<Article> parseArticlesFromJson(JsonNode json) {
        List<Article> articles = new ArrayList<>();
        for (final JsonNode objNode : json) {
            Article article = createArticleFromJsonNode(objNode);
            articles.add(article);
        }
        return articles;
    }

    private Article parseSingleArticleFromJson(JsonNode json) {
        Article article = null;
        for (final JsonNode objNode : json) {
            article = createArticleFromJsonNode(objNode);
        }
        return article;
    }

    private Article createArticleFromJsonNode(JsonNode objNode) {
        int id = objNode.get("id").asInt();
        String code = objNode.get("code").asText();
        String libelle = objNode.get("libelle").asText();
        int id_categorie = objNode.get("id_categorie").asInt();
        return new Article(id,code,libelle,id_categorie);
    }

}
