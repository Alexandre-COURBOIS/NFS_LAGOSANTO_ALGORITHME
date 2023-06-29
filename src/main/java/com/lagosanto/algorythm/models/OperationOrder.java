package com.lagosanto.algorythm.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OperationOrder {
    private String codeArticle;
    private int productQuantity;
    private int order;

    public OperationOrder(String codeArticle, int productQuantity, int order) {
        this.codeArticle = codeArticle;
        this.productQuantity = productQuantity;
        this.order = order;
    }
}
