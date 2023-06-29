package com.lagosanto.algorythm;

import com.lagosanto.algorythm.models.Recipe;
import com.lagosanto.algorythm.services.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
public class RecipeServiceTest {

    @Autowired
    private RecipeService recipeService;

    @Test
    public void testGetAll() throws IOException {
        List<Recipe> recipes = recipeService.getAllRecipe();
        assertThat(recipes).isNotEmpty();
    }

    @Test
    public void testGetRecipe() throws IOException {
        Recipe recipe = recipeService.getRecipe(152);
        assertThat(recipe.getId_article()).isEqualTo(152);
        assertThat(recipe.getId_operation()).isEqualTo(1);
        assertThat(recipe.getId_composant1()).isEqualTo(258);
        assertThat(recipe.getQuantite1()).isEqualTo(1);
    }
}
