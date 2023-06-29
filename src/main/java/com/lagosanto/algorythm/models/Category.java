package com.lagosanto.algorythm.models;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Category {
    private int id;

    private String code;

    private String libelle;

    public Category(int id, String code, String libelle) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
    }
}
