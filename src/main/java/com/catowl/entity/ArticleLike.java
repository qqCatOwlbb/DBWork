package com.catowl.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("文章点赞实体类")
public class ArticleLike {
    @ApiModelProperty("文章点赞ID")
    private Long id;  // 主键ID

    @ApiModelProperty("文章ID")
    private Long article_id;  // 文章ID（外键）

    @ApiModelProperty("用户ID")
    private Long user_id;  // 用户ID（外键）

    @ApiModelProperty("点赞时间")
    private LocalDateTime created_at;  // 点赞时间
}
