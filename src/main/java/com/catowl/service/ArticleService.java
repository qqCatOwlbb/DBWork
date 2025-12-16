package com.catowl.service;

import com.catowl.dto.ArticleDTO;
import com.catowl.entity.Article;

import java.util.List;
import java.util.Map;

public interface ArticleService {
    List<ArticleDTO> getArticlesByLike(int page, int pageSize);

    List<ArticleDTO> getArticlesByView(int page, int pageSize);

    void incrementViewCount(Long articleId);

    int deleteArticle(Long articleId);

    void publishArticle(Article article);

    Map<String,Object> getArticleById(Long article_id);

    List<Article> list();

    Boolean updateNumById(Article article);
}
