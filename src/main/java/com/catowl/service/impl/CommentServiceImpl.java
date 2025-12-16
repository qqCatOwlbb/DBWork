package com.catowl.service.impl;

import com.catowl.dto.CommentGetDTO;
import com.catowl.entity.Comment;
import com.catowl.entity.User;
import com.catowl.exception.InternetServerException;
import com.catowl.mapper.CommentMapper;
import com.catowl.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    public void insertComment(Comment comment){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User loginUser = (User) authentication.getPrincipal();
            Long userId = loginUser.getId();
            comment.setUser_id(userId);
            commentMapper.addComment(comment);
        }catch (RuntimeException e){
            throw new InternetServerException("发表评论失败，数据库插入时发生异常");
        }
    }

    public void deleteComment(Long comment_id){
        try{
            commentMapper.deleteComment(comment_id);
        }catch (RuntimeException e){
            throw new InternetServerException("删除评论失败，数据库更新时发生异常"+e);
        }
    }

    public Map<String, Object> selectCommentById(Long comment_id){
        try{
            return commentMapper.selectCommentById(comment_id);
        }catch (RuntimeException e){
            throw new InternetServerException("查询评论失败，数据库查询时发生异常");
        }
    }

    public List<CommentGetDTO> selectParentComment(Long article_id){
        try{
            return commentMapper.selectParentComment(article_id);
        }catch (RuntimeException e){
            throw new InternetServerException("查询父评论失败，数据库查询时发生异常");
        }
    }

    public List<CommentGetDTO> selectSonComment(Long parent_comment_id){
        try{
            return commentMapper.selectSonComment(parent_comment_id);
        }catch (RuntimeException e){
            throw new InternetServerException("查询子评论失败，数据库查询时发生异常");
        }
    }
}
