package com.lagosanto.algorythm;

import com.lagosanto.algorythm.models.Article;
import com.lagosanto.algorythm.services.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = Main.class)
public class ArticleServiceTest {
    @Autowired
    private ArticleService articleService;

    @Test
    public void testGetAll() throws IOException {
        List<Article> articles = articleService.getAllArticles();
        assertThat(articles).isNotEmpty();
    }

    @Test
    public void testGetArticle() throws IOException {
        Article article = articleService.getArticle(7);
        assertThat(article.getLibelle()).isEqualTo("exercitationem");
        assertThat(article.getCode()).isEqualTo("P997263787");
        assertThat(article.getId_categorie()).isEqualTo(2);
    }
}
