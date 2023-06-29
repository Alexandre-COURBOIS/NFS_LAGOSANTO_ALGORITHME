package com.lagosanto.algorythm.models;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Article {
    private int id;
    private String code;
    private String libelle;
    private int id_categorie;

    public Article(int id, String code, String libelle, int id_categorie) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.id_categorie = id_categorie;
    }
}
