package com.lagosanto.algorythm;

import com.lagosanto.algorythm.models.Operation;
import com.lagosanto.algorythm.services.OperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
public class OperationServiceTest {

    @Autowired
    private OperationService operationService;

    @Test
    public void testGetAll() throws IOException {
        List<Operation> operations = operationService.getAllOperations();
        assertThat(operations).isNotEmpty();
    }

    @Test
    public void testGetOperation() throws IOException {
        Operation operation = operationService.getOperation(7);
        assertThat(operation.getLibelle()).isEqualTo("Pliage");
        assertThat(operation.getCode()).isEqualTo("O04");
        assertThat(operation.getDelai()).isEqualTo(4);
        assertThat(operation.getDelaiInstallation()).isEqualTo(14);
    }

    @Test
    public void testGetOperationsByWorkUnit() throws IOException {
        List<Operation> operations = operationService.getOperationsByWorkUnit(2);
        assertThat(operations).isNotEmpty();
        assertThat(operations.get(2).getId()).isEqualTo(4);
        assertThat(operations.get(2).getCode()).isEqualTo("O06");
        assertThat(operations.get(2).getLibelle()).isEqualTo(null);
    }
}
