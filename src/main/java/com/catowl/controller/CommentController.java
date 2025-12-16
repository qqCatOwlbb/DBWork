package com.catowl.controller;

import com.catowl.dto.CommentGetDTO;
import com.catowl.dto.CommentPublishDTO;
import com.catowl.entity.Comment;
import com.catowl.entity.User;
import com.catowl.exception.BadRequestException;
import com.catowl.service.CommentService;
import com.catowl.utils.APIResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "评论管理", description = "提供评论的增删改查功能，包括发表评论、删除评论、获取评论列表等")
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ApiOperation(
        value = "删除评论",
        notes = "用户只能删除自己的评论，需要提供评论ID。删除成功返回成功消息，失败则抛出异常。"
    )
    @DeleteMapping("/comment/delete")
    public APIResultUtil<String> deleteComment(
        @ApiParam(value = "评论ID", required = true, example = "1")
        @RequestParam("comment_id") Long comment_id
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) authentication.getPrincipal();
        Long userId = loginUser.getId();
        Map<String,Object> map = commentService.selectCommentById(comment_id);
        Long user_id = (Long) map.get("user_id");
        if(!userId.equals(user_id)){
            throw new BadRequestException("仅能删除自己的评论");
        }
        commentService.deleteComment(comment_id);
        return APIResultUtil.success("评论删除成功");
    }

    @ApiOperation(
        value = "发表评论",
        notes = "发表新评论，需要提供文章ID和评论内容。评论发表成功返回成功消息，失败则抛出异常。",
        response = APIResultUtil.class
    )
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/comment/add")
    public APIResultUtil<String> addComment(
        @ApiParam(value = "评论信息", required = true)
        @RequestBody CommentPublishDTO commentPublishDTO
    ) {
        Comment comment = new Comment();
        commentPublishDTO.setComment(comment);
        if (comment.getArticle_id() == null || comment.getContent() == null) {
            throw new BadRequestException("文章id和评论内容不能为空");
        }
        commentService.insertComment(comment);
        return APIResultUtil.success("评论发表成功");
    }

    @ApiOperation(
        value = "获取评论详情",
        notes = "根据评论ID获取评论的详细信息，包括评论内容、发表时间、评论者信息等。",
        response = APIResultUtil.class
    )
    @GetMapping("/comment/getById")
    public APIResultUtil<Map<String,Object>> selectCommentById(
        @ApiParam(value = "评论ID", required = true, example = "1")
        @RequestParam("comment_id") Long comment_id
    ) {
        return APIResultUtil.success(commentService.selectCommentById(comment_id));
    }

    @ApiOperation(
        value = "获取文章评论列表",
        notes = "获取指定文章的所有父评论，返回评论列表，包含评论内容和评论者信息。",
        response = APIResultUtil.class
    )
    @GetMapping("/comment/parent")
    public APIResultUtil<List<CommentGetDTO>> selectParentComment(
        @ApiParam(value = "文章ID", required = true, example = "1")
        @RequestParam("article_id") Long article_id
    ) {
        return APIResultUtil.success(commentService.selectParentComment(article_id));
    }

    @ApiOperation(
        value = "获取子评论列表",
        notes = "获取指定父评论的所有子评论，返回评论列表，包含评论内容和评论者信息。",
        response = APIResultUtil.class
    )
    @GetMapping("/comment/son")
    public APIResultUtil<List<CommentGetDTO>> selectSonComment(
        @ApiParam(value = "父评论ID", required = true, example = "1")
        @RequestParam("parent_comment_id") Long parentId
    ) {
        return APIResultUtil.success(commentService.selectSonComment(parentId));
    }
}
