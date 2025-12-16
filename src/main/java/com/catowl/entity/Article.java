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
@ApiModel("文章实体类")
public class Article {
    @ApiModelProperty("文章id")
    private Long id;

    @ApiModelProperty("文章标题")
    private String title;

    @ApiModelProperty("文章内容")
    private String content;

    @ApiModelProperty("作者的用户ID")
    private Long author_id;  // 作者ID（外键，关联 users 表）

    @ApiModelProperty("文章浏览量")
    private Integer view_count;

    @ApiModelProperty("文章点赞量")
    private Integer like_count;

    @ApiModelProperty("文章状态（0=草稿，1=已发布）")
    private Integer status;

    @ApiModelProperty("创建时间")
    private LocalDateTime created_at;

    @ApiModelProperty("更新时间")
    private LocalDateTime updated_at;
}
