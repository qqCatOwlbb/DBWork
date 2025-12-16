package com.catowl.controller;

import com.catowl.dto.ArticleDTO;
import com.catowl.dto.ArticlePublishDTO;
import com.catowl.entity.Article;
import com.catowl.entity.User;
import com.catowl.exception.BadRequestException;
import com.catowl.service.ArticleService;
import com.catowl.utils.APIResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "文章管理")
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @ApiOperation("按点赞量从高到低获取文章")
    @GetMapping("/article/select/bylike")
    public APIResultUtil<List<ArticleDTO>> selArtcleListByLike(@RequestParam("page") int page,
                                                                         @RequestParam("pageSize") int pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new BadRequestException("页码与页面大小应为正整数");
        }
        List<ArticleDTO> articles = articleService.getArticlesByLike(page, pageSize);
        if (articles == null) {
            return APIResultUtil.error("没有更多文章");
        }
        return APIResultUtil.success(articles);
    }

    @ApiOperation("按点击量从高到低获取文章")
    @GetMapping("/article/select/byview")
    public APIResultUtil<List<ArticleDTO>> selArtcleListByView(@RequestParam("page") int page,
                                                                        @RequestParam("pageSize") int pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new BadRequestException("页码与页面大小应为正整数");
        }
        List<ArticleDTO> articles = articleService.getArticlesByView(page, pageSize);
        if (articles == null) {
            return APIResultUtil.error("没有更多文章");
        }
        return APIResultUtil.success(articles);
    }

    @ApiOperation("无用已废弃")
    @PostMapping("/article/view")
    public APIResultUtil<Map<String,Object>> getById(@RequestParam("article_id") Long articleId) {
        Map<String,Object> article = articleService.getArticleById(articleId);
        return APIResultUtil.success(article);
    }

    @ApiOperation("发表文章")
    @PostMapping("/article/publish")
    public APIResultUtil<String> publishArticle(@RequestBody ArticlePublishDTO articlePublishDTO) {
        Article article=new Article();
        articlePublishDTO.setArticle(article);
        if ((article.getTitle() == null || article.getTitle().isEmpty()) || article.getContent() == null || article.getContent().isEmpty()) {
            throw new BadRequestException("文章标题和内容不能为空");
        }
        articleService.publishArticle(article);
        return APIResultUtil.success("文章发表成功");
    }

    @ApiOperation("删除自己发表的某篇文章文章")
    @DeleteMapping("/article/delete")
    public APIResultUtil<String> deleteArticle(@RequestParam("article_id") Long article_id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) authentication.getPrincipal();
        Long userId = loginUser.getId();
        Map<String, Object> map = articleService.getArticleById(article_id);
        Long author_id = (Long) map.get("author_id");
        if (!userId.equals(author_id)) {
            throw new BadRequestException("仅能删除自己写的文章");
        }
        articleService.deleteArticle(article_id);
        return APIResultUtil.success("文章删除成功");
    }
}
