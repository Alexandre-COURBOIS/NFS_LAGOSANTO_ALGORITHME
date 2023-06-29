package com.lagosanto.algorythm.models;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Operation {
    private int id;
    private String code;
    private String libelle;
    private int delai;
    private int delaiInstallation;

    public Operation(int id, String code, String libelle, int delai, int delaiInstallation) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.delai = delai;
        this.delaiInstallation = delaiInstallation;
    }

    public Operation(int id, String code) {
        this.id = id;
        this.code = code;
    }
}
