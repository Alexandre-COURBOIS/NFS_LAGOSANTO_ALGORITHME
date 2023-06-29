package com.lagosanto.algorythm.models;

public class ArticleQuantity {
        private Article article;
        private Integer quantity;

        public ArticleQuantity(Article article, Integer quantity) {
            this.article = article;
            this.quantity = quantity;
        }

        public Article getArticle() {
            return article;
        }

        public void setArticle(Article recipe) {
            this.article = recipe;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }