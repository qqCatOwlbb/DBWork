package com.catowl.service;

import com.catowl.dto.ArticleDTO;
import com.catowl.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    User selectUser();
    List<ArticleDTO> selectArticleByAuthor();
    List<ArticleDTO> selectMyLike();
    int updateUserInfo(User user);
    User getInfoById(Long id);
    String login(User user);
    void logout();
    int insertUser(User user);
    String handleAvatarUpload(MultipartFile file);
    String generateFileName(MultipartFile file);
    void deleteOldAvatar(String oldAvatarUrl);
}
