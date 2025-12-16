package com.catowl.dto;

import com.catowl.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(
    value = "用户登录或注册请求",
    description = "用于用户登录和注册的请求参数，前端可忽视"
)
public class UserLoginDTO {
    @ApiModelProperty(
        value = "用户名",
        required = true,
        example = "张三",
        notes = "用户名长度建议在3-20个字符之间，只能包含字母、数字和下划线",
        position = 1
    )
    private String username;

    @ApiModelProperty(
        value = "密码",
        required = true,
        example = "123456",
        notes = "密码长度建议在6-20个字符之间，必须包含字母和数字",
        position = 2
    )
    private String password;

    @ApiModelProperty(
            hidden = true
    )
    public void setUser(User user) {
        user.setUsername(this.username);
        user.setPassword(this.password);
    }
}
