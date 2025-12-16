package com.catowl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {
    int addArticleLike(@Param("article_id") Long article_id, @Param("user_id") Long user_id);

    int deleteArticleLike(@Param("article_id") Long article_id, @Param("user_id") Long user_id);

    int updateLikeCount(@Param("article_id") Long article_id, @Param("like_count") int like_count);

    int getLikeById(@Param("article_id") Long articleId);
}
