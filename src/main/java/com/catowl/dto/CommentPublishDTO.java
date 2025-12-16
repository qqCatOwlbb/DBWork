package com.catowl.dto;

import com.catowl.entity.Comment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(
    value = "评论发布请求",
    description = "用于发布新评论的请求参数，包含文章ID、评论内容等信息"
)
public class CommentPublishDTO {
    @ApiModelProperty(
        value = "被评论的文章ID",
        required = true,
        example = "1",
        notes = "必须提供有效的文章ID"
    )
    private Long article_id;

    @ApiModelProperty(
        value = "评论内容",
        required = true,
        example = "这是一条评论",
        notes = "评论内容不能为空，建议长度在1-500个字符之间"
    )
    private String content;

    @ApiModelProperty(
        value = "父评论ID",
        example = "3",
        notes = "如果是回复其他评论，则需要提供父评论ID；如果是直接评论文章，则为null"
    )
    private Long parent_comment_id;

    @ApiModelProperty(hidden = true)
    public void setComment(Comment comment) {
        comment.setArticle_id(this.article_id);
        comment.setContent(this.content);
        comment.setParent_comment_id(this.parent_comment_id);
    }
}
