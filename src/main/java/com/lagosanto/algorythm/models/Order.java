package com.lagosanto.algorythm.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Order {

    private String codeWorkUnit;

    private List<OperationOrder> productOperations;

    public Order(String codeWorkUnit, List<OperationOrder> productOperations) {
        this.codeWorkUnit = codeWorkUnit;
        this.productOperations = productOperations;
    }

    @Override
    public String toString() {
        return "Order{" +
                "codeWorkUnit='" + codeWorkUnit + '\'' +
                ", operations=" + productOperations +
                '}';
    }
}
