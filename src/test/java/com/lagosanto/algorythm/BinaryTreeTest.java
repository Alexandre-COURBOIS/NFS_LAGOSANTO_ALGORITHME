package com.lagosanto.algorythm;

import com.lagosanto.algorythm.services.ProductionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
public class BinaryTreeTest {

    @Autowired
    ProductionService productionService;

    @Test
    public void test() throws IOException {
        productionService.launchProduction(120,1);
        assertThat(1).isEqualTo(1);
    }
}
