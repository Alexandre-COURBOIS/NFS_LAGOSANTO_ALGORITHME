package com.lagosanto.algorythm;

import com.lagosanto.algorythm.models.Category;
import com.lagosanto.algorythm.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void testGetAll() throws IOException {
        List<Category> categories = categoryService.getAllCategories();
        assertThat(categories).isNotEmpty();
    }

    @Test
    public void testGetCategory() throws IOException {
        Category category = categoryService.getCategory(6);
        assertThat(category.getLibelle()).isEqualTo("006");
        assertThat(category.getCode()).isEqualTo("C056");
    }
}
