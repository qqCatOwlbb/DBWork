package com.catowl.mapper;

import com.catowl.dto.CommentGetDTO;
import com.catowl.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {
    void addComment(Comment comment);
    void deleteComment(Long comment_id);
    Map<String,Object> selectCommentById(Long comment_id);
    List<CommentGetDTO> selectParentComment(@Param("article_id") Long article_id);
    List<CommentGetDTO> selectSonComment(@Param("parent_comment_id") Long parentId);
}
