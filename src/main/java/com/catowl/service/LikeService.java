package com.catowl.service;

public interface LikeService {
    boolean likeArticle(Long articleId);
    boolean unlikeArticle(Long articleId);
    void syncLikesToMySQL();
    int getArticleLikeCount(Long articleId);
}
