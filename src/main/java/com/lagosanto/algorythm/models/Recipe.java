package com.lagosanto.algorythm.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Recipe {
    private int id_article;
    private int id_operation;
    private int id_composant1;
    private int quantite1;

    private int id_composant2;
    private int quantite2;

    public Recipe(int id_article, int id_operation, int id_composant1, int quantite1) {
        this.id_article = id_article;
        this.id_operation = id_operation;
        this.id_composant1 = id_composant1;
        this.quantite1 = quantite1;
    }

    public Recipe(int id_article, int id_operation, int id_composant1, int quantite1, int id_composant2, int quantite2) {
        this.id_article = id_article;
        this.id_operation = id_operation;
        this.id_composant1 = id_composant1;
        this.quantite1 = quantite1;
        this.id_composant2 = id_composant2;
        this.quantite2 = quantite2;
    }
}
