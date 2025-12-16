package com.catowl.controller;

import com.catowl.dto.ArticleDTO;
import com.catowl.dto.UserLoginDTO;
import com.catowl.dto.UserUpdateDTO;
import com.catowl.entity.Article;
import com.catowl.entity.User;
import com.catowl.exception.BadRequestException;
import com.catowl.service.UserService;
import com.catowl.utils.APIResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Api(
    value = "用户管理接口",
    tags = {"用户管理"},
    description = "提供用户的注册、登录、信息更新等操作",
    produces = "application/json",
    consumes = "application/json"
)
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api")
public class UserController {
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    @Autowired
    private UserService userService;

    @ApiOperation(
        value = "获取用户发布的文章",
        notes = "获取当前登录用户发布的所有文章列表",
        response = APIResultUtil.class,
        produces = "application/json",
        httpMethod = "GET"
    )
    @GetMapping("/user/getart")
    public APIResultUtil<List<ArticleDTO>> myArticle(){
        return APIResultUtil.success(userService.selectArticleByAuthor());
    }

    @ApiOperation(
        value = "获取用户点赞的文章",
        notes = "获取当前登录用户点赞过的所有文章列表",
        response = APIResultUtil.class,
        produces = "application/json",
        httpMethod = "GET"
    )
    @GetMapping("/user/mylike")
    public APIResultUtil<List<ArticleDTO>> myLike(){
        return APIResultUtil.success(userService.selectMyLike());
    }

    @ApiOperation(
        value = "获取用户信息",
        notes = "根据用户ID获取用户的基本信息（已废弃，使用联表查询替代）",
        response = APIResultUtil.class,
        produces = "application/json",
        httpMethod = "GET",
        hidden = true
    )
    @GetMapping("user/getinfobyid")
    public APIResultUtil<User> getInfoById(
        @ApiParam(value = "用户ID", required = true, example = "1") @RequestParam("id") Long id
    ){
        return APIResultUtil.success(userService.getInfoById(id));
    }

    @ApiOperation(
        value = "用户登录",
        notes = "使用用户名和密码进行登录，返回JWT token",
        response = APIResultUtil.class,
        produces = "application/json",
        httpMethod = "POST"
    )
    @PostMapping("/login")
    public APIResultUtil<String> login(
        @ApiParam(value = "登录信息", required = true) @RequestBody UserLoginDTO userLoginDTO
    ) {
        User user = new User();
        userLoginDTO.setUser(user);
        if(user.getUsername()==null||user.getPassword()==null){
            throw new BadRequestException("用户名或密码不能为空");
        }
        String token = userService.login(user);
        return APIResultUtil.success(token, "登录成功");
    }

    @ApiOperation(
        value = "用户登出",
        notes = "清除用户的登录状态和token",
        response = APIResultUtil.class,
        produces = "application/json",
        httpMethod = "DELETE"
    )
    @DeleteMapping("/logout")
    public APIResultUtil<String> logout(){
        userService.logout();
        return APIResultUtil.success("登出成功");
    }

    @ApiOperation(
        value = "用户注册",
        notes = "注册新用户，需要提供用户名和密码",
        response = APIResultUtil.class,
        produces = "application/json",
        httpMethod = "POST"
    )
    @PostMapping("/register")
    public APIResultUtil<String> register(
        @ApiParam(value = "注册信息", required = true) @RequestBody UserLoginDTO userLoginDTO
    ){
        User user = new User();
        userLoginDTO.setUser(user);
        if((user.getUsername()==null||user.getUsername().isEmpty())||(user.getPassword()==null||user.getPassword().isEmpty())){
            throw new BadRequestException("用户名或密码不能为空");
        }
        userService.insertUser(user);
        return APIResultUtil.success("注册成功");
    }

    @ApiOperation(
        value = "更新用户信息",
        notes = "更新用户的用户名、密码或简介信息",
        response = APIResultUtil.class,
        produces = "application/json",
        httpMethod = "POST"
    )
    @PostMapping("/update")
    public APIResultUtil<String> update(
        @ApiParam(value = "用户信息", required = true) @RequestBody UserUpdateDTO userUpdateDTO
    ){
        User user = new User();
        userUpdateDTO.setUser(user);
        userService.updateUserInfo(user);
        boolean isUsernameModified = user.getUsername() != null && !user.getUsername().isEmpty();
        boolean isPasswordModified = user.getPassword() != null && !user.getPassword().isEmpty();
        if (isUsernameModified || isPasswordModified) {
            return APIResultUtil.success("更新成功，用户名或密码已修改，请重新登录");
        } else {
            return APIResultUtil.success("更新成功");
        }
    }

    @ApiOperation(
        value = "更新用户头像",
        notes = "上传并更新用户的头像图片，支持jpg、png格式，大小不超过2MB",
        response = APIResultUtil.class,
        produces = "application/json",
        httpMethod = "POST"
    )
    @PostMapping("/updateavatar")
    public APIResultUtil<String> updateAvatar(
        @ApiParam(value = "头像文件", required = true) @RequestParam(value = "file", required = false) MultipartFile file
    ){
        if(file!=null&&file.isEmpty()){
            if(file.getSize() > MAX_FILE_SIZE){
                throw new BadRequestException("文件过大，最大允许2MB");
            }
        }
        userService.handleAvatarUpload(file);
        return APIResultUtil.success("更新头像成功");
    }

    @ApiOperation(
        value = "获取用户信息",
        notes = "获取当前登录用户的完整信息",
        response = APIResultUtil.class,
        produces = "application/json",
        httpMethod = "GET"
    )
    @GetMapping("/user/getinfo")
    public APIResultUtil<User> getinfo(){
        User user = userService.selectUser();
        return APIResultUtil.success(user);
    }
}
