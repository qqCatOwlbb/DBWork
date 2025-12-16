package com.catowl.dto;

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
    value = "评论信息响应",
    description = "评论的详细信息，包含评论内容、评论者信息、评论时间等"
)
public class CommentGetDTO {
    @ApiModelProperty(
        value = "评论ID",
        example = "1",
        notes = "评论的唯一标识符"
    )
    private Long id;

    @ApiModelProperty(
        value = "文章ID",
        example = "1",
        notes = "被评论的文章ID"
    )
    private Long article_id;

    @ApiModelProperty(
        value = "评论者ID",
        example = "1",
        notes = "发表评论的用户ID"
    )
    private Long user_id;

    @ApiModelProperty(
        value = "评论内容",
        example = "这是一条评论",
        notes = "评论的具体内容"
    )
    private String content;

    @ApiModelProperty(
        value = "父评论ID",
        example = "3",
        notes = "如果是回复其他评论，则为父评论ID；如果是直接评论文章，则为null"
    )
    private Long parent_comment_id;

    @ApiModelProperty(
        value = "评论时间",
        example = "2024-03-08T10:30:00",
        notes = "评论发表的时间"
    )
    private LocalDateTime create_at;

    @ApiModelProperty(
        value = "评论者用户名",
        example = "johndoe",
        notes = "发表评论的用户名"
    )
    private String username;

    @ApiModelProperty(
        value = "评论者头像",
        example = "/uploads/avatar-123.jpg",
        notes = "评论者的头像URL，可能为null"
    )
    private String avatar;
}
