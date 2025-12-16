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
@ApiModel(
    value = "评论实体",
    description = "评论的基本信息，包含评论内容、关联的文章和用户信息等"
)
public class Comment {
    @ApiModelProperty(
        value = "评论ID",
        example = "1",
        notes = "评论的唯一标识符"
    )
    private Long id;  // 评论ID（主键）

    @ApiModelProperty(
        value = "文章ID",
        example = "1",
        notes = "被评论的文章ID"
    )
    private Long article_id;  // 关联的文章ID（外键）

    @ApiModelProperty(
        value = "评论者ID",
        example = "1",
        notes = "发表评论的用户ID"
    )
    private Long user_id;  // 发表评论的用户ID（外键）

    @ApiModelProperty(
        value = "评论内容",
        example = "这是一条评论",
        notes = "评论的具体内容"
    )
    private String content;  // 评论内容

    @ApiModelProperty(
        value = "父评论ID",
        example = "3",
        notes = "如果是回复其他评论，则为父评论ID；如果是直接评论文章，则为null"
    )
    private Long parent_comment_id;  // 父评论ID（如果是根评论，则为null）

    @ApiModelProperty(
        value = "评论时间",
        example = "2024-03-08T10:30:00",
        notes = "评论发表的时间"
    )
    private LocalDateTime create_at;  // 创建时间
}
