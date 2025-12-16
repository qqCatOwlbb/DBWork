package com.catowl.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(
    value = "用户实体类",
    description = "用户的基本信息，包含用户名、密码等字段",
    parent = Object.class
)
public class User implements UserDetails {
    @ApiModelProperty(
        value = "用户id",
        readOnly = true,
        position = 1
    )
    private Long id;

    @ApiModelProperty(
        value = "用户名",
        required = true,
        example = "johndoe",
        position = 2,
        notes = "用户名长度建议在3-20个字符之间"
    )
    private String username;

    @ApiModelProperty(
        value = "用户密码",
        required = true,
        example = "password123",
        position = 3,
        notes = "密码长度建议在6-20个字符之间"
    )
    private String password;

    @ApiModelProperty(
        value = "头像地址",
        example = "/uploads/avatar-123.jpg",
        position = 4,
        notes = "用户头像的URL地址，可以为空"
    )
    private String avatar;

    @ApiModelProperty(
        value = "个人简介",
        example = "热爱编程的开发者",
        position = 5,
        notes = "用户的个人简介，可以为空"
    )
    private String bio;

    @ApiModelProperty(
        value = "账号创建时间",
        readOnly = true,
        position = 6,
        dataType = "date-time"
    )
    private Date created_at;

    @ApiModelProperty(
        value = "账号更新时间",
        readOnly = true,
        position = 7,
        dataType = "date-time"
    )
    private Date updated_at;

    @ApiModelProperty(hidden = true)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // 这里可以实现角色权限
    }

    @ApiModelProperty(hidden = true)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @ApiModelProperty(hidden = true)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @ApiModelProperty(hidden = true)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @ApiModelProperty(hidden = true)
    @Override
    public boolean isEnabled(){
        return true;
    }
}