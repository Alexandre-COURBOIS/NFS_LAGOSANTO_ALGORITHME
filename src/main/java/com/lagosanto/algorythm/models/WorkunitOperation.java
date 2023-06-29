package com.lagosanto.algorythm.models;

import java.util.List;

public class WorkunitOperation {

    WorkUnit workUnit;
    List<Integer> operations;

    public WorkunitOperation(WorkUnit workUnit, List<Integer> operations) {
        this.workUnit = workUnit;
        this.operations = operations;
    }

    public WorkUnit getWorkUnit() {
        return workUnit;
    }

    public void setWorkUnit(WorkUnit workUnit) {
        this.workUnit = workUnit;
    }

    public List<Integer> getOperations() {
        return operations;
    }

    public void setOperations(List<Integer> operations) {
        this.operations = operations;
    }
}
