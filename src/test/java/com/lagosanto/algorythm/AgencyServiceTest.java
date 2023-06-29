package com.lagosanto.algorythm;

import com.lagosanto.algorythm.models.Agency;
import com.lagosanto.algorythm.services.AgencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
public class AgencyServiceTest {

    @Autowired
    private AgencyService agencyService;

    @Test
    public void testGetAll() throws IOException {
        List<Agency> agencies = agencyService.getAllAgencies();
        assertThat(agencies).isNotEmpty();
    }

    @Test
    public void testGetCategory() throws IOException {
        Agency agency = agencyService.getAgency(7);
        assertThat(agency.getLibelle()).isEqualTo("Haute-Garonne");
        assertThat(agency.getCode()).isEqualTo("A0973");
    }

}
