package com.lagosanto.algorythm;

import com.lagosanto.algorythm.services.ProductionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);
        ProductionService productionService = context.getBean(ProductionService.class);
        try {
            productionService.launchProduction(120, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}