package com.lagosanto.algorythm.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkUnit {
    private int id;
    private String code;
    private String libelle;

    public WorkUnit(int id, String code, String libelle) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
    }
}
