package com.lagosanto.algorythm.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Error {
    private int status;
    private String message;

    public Error(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
