package com.catowl.dto;

import com.catowl.entity.Article;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("发布文章DTO")
public class ArticlePublishDTO {

    @ApiModelProperty(
            value = "文章标题",
            example = "这是一个标题"
    )
    private String title;

    @ApiModelProperty(
            value = "文章内容",
            example = "这是文章的内容"
    )
    private String content;

    @ApiModelProperty(hidden = true)
    public void setArticle(Article article) {
        article.setTitle(this.title);
        article.setContent(this.content);
    }
}
