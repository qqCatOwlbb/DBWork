package com.catowl.controller;

import com.catowl.entity.User;
import com.catowl.service.LikeService;
import com.catowl.utils.APIResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Api(tags = "点赞管理")
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @ApiOperation("根据文章id点赞一篇文章")
    @PostMapping("/article/like")
    public APIResultUtil<String> addLike(@RequestParam("article_id") Long article_id){
        likeService.likeArticle(article_id);
        return APIResultUtil.success("点赞成功");
    }

    @ApiOperation("根据文章id取消点赞")
    @DeleteMapping("/article/like")
    public APIResultUtil<String> deleteLike(@RequestParam("article_id") Long article_id){
        likeService.unlikeArticle(article_id);
        return APIResultUtil.success("取消点赞成功");
    }

    @ApiOperation("根据文章id获取它的点赞数")
    @GetMapping("/article/getlike")
    public APIResultUtil<Integer> getLike(@RequestParam("article_id") Long article_id){
        int likeCount = likeService.getArticleLikeCount(article_id);
        return APIResultUtil.success(likeCount);
    }
}

