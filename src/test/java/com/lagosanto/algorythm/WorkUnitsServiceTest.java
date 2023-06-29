package com.lagosanto.algorythm;

import com.lagosanto.algorythm.models.WorkUnit;
import com.lagosanto.algorythm.services.WorkUnitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
public class WorkUnitsServiceTest {

    @Autowired
    private WorkUnitService workUnitService;


    @Test
    public void testGetAll() throws IOException {
        List<WorkUnit> workUnits = workUnitService.getAllWorkUnits();
        assertThat(workUnits).isNotEmpty();
    }

    @Test
    public void testGetWorkUnit() throws IOException {
        WorkUnit workUnit = workUnitService.getWorkUnit(7);
        assertThat(workUnit.getLibelle()).isEqualTo("352980800");
        assertThat(workUnit.getCode()).isEqualTo("W024");
    }

    @Test
    public void testgetWorkUnitsByOperation() throws IOException {
        List<WorkUnit> workUnits = workUnitService.getWorkUnitsByOperation(8);
        assertThat(workUnits).isNotEmpty();
    }
}
