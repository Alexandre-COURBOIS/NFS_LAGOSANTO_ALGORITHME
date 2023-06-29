package com.lagosanto.algorythm;

import com.lagosanto.algorythm.services.ProductionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.println("Veuillez fournir deux arguments : idArticle et quantité de l'article");
            System.exit(1);
        }

        int idArticle = Integer.parseInt(args[0]);
        int quantity = Integer.parseInt(args[1]);

        ApplicationContext context = SpringApplication.run(Main.class, args);
        ProductionService productionService = context.getBean(ProductionService.class);
        try {
            productionService.launchProduction(idArticle, quantity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}