package com.catowl.dto;

import com.catowl.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(
    value = "用户信息更新请求",
    description = "用于更新用户信息的请求参数，所有字段都是可选的"
)
public class UserUpdateDTO {
    @ApiModelProperty(
        value = "新用户名",
        example = "newusername",
        notes = "用户名长度建议在3-20个字符之间，只能包含字母、数字和下划线",
        position = 1
    )
    private String username;

    @ApiModelProperty(
        value = "新密码",
        example = "newpassword123",
        notes = "密码长度建议在6-20个字符之间，必须包含字母和数字",
        position = 2
    )
    private String password;

    @ApiModelProperty(
        value = "个人简介",
        example = "热爱编程的开发者",
        notes = "用户的个人简介，最大长度200个字符",
        position = 3
    )
    private String bio;

    @ApiModelProperty(
            hidden = true
    )
    public void setUser(User user) {
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setBio(this.bio);
    }
}
