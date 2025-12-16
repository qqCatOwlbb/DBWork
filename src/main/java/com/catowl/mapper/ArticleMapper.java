package com.catowl.mapper;

import com.catowl.dto.ArticleDTO;
import com.catowl.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper {
    List<Article> list();
    List<ArticleDTO> articleListByLike(@Param("limit") int limit, @Param("offset") int offset);
    List<ArticleDTO> articleListByView(@Param("limit") int limit, @Param("offset") int offset);
    int incrementViewCount(@Param("article_id") Long articleId);
    int deleteArticle(@Param("article_id") Long articleId);
    int publishArticle(Article article);
    Map<String,Object> getArticleById(Long article_id);
    int updateNumById(Article article);
}
