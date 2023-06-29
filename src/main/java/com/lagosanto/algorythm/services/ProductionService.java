package com.lagosanto.algorythm.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.lagosanto.algorythm.models.Order;
import com.lagosanto.algorythm.models.Recipe;
import com.lagosanto.algorythm.models.Tuple;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProductionService {

    @Value("${project.api.urlbase}")
    private String URLBASE;

    private static final String URL_PRODUCTION = "production";

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private BinaryTreeService binaryTreeService;

    public JSONObject launchProduction(int idArticle, int qty) throws IOException {
        //Récupération de la recette de l'article
        Recipe recipe = recipeService.getRecipe(idArticle);

        //Contient BinaryTree & ListeWorkUnit
        Tuple tuple = binaryTreeService.map(recipe);

        List<Order> orderList = binaryTreeService.prepareOrders(tuple.getTree().getRoot(), qty, tuple.getListWorkUnits());
        Connection.Response response = makeRequest(orderList);

        JSONObject reponseJson =  new JSONObject(response.body());

        System.out.println("Status : " +reponseJson.get("status"));
        System.out.println("Delay : "+reponseJson.get("delay") + "ms");

        return reponseJson;
    }

    private Connection.Response makeRequest(List<Order> orderList) throws IOException {
        String json = formatOrderListToJson(orderList);
        return Jsoup.connect(URLBASE + URL_PRODUCTION)
                .timeout(0)
                .method(Connection.Method.POST)
                .header("Content-Type", "application/json;charset=UTF-8")
                .ignoreContentType(true)
                .requestBody(json)
                .execute();
    }

    private String formatOrderListToJson(List<Order> orderList) throws IOException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(orderList);
        return json.replace("\r\n","");
    }
}
