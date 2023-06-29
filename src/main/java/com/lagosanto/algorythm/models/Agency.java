package com.lagosanto.algorythm.models;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Agency {
    private int id;

    private String code;

    private String libelle;

    public Agency(int id, String code, String libelle) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
    }
}
