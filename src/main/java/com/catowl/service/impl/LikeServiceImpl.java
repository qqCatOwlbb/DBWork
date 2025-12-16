package com.catowl.service.impl;

import com.catowl.entity.User;
import com.catowl.exception.GlobalExceptionHandler;
import com.catowl.mapper.ArticleMapper;
import com.catowl.mapper.LikeMapper;

import com.catowl.service.ArticleService;
import com.catowl.service.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private LikeMapper likeMapper;
    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String LIKE_KEY_PREFIX = "article:like:";
    private static final String COUNT_KEY_PREFIX = "article:like_count:";
    private static final String SYNCED_KEY_PREFIX = "article:like_synced:";

    //点赞
    public boolean likeArticle(Long articleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) authentication.getPrincipal();
        Long userId = loginUser.getId();
        String likeKey = LIKE_KEY_PREFIX + articleId;
        String countKey = COUNT_KEY_PREFIX + articleId;
        String syncedKey = SYNCED_KEY_PREFIX + articleId + ":" + userId;

        // 判断是否已经点过赞
        Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId.toString());
        if (Boolean.TRUE.equals(isMember)) {
            // 如果状态是2，说明用户之前点了赞又取消了，现在反悔再点
            String synced = redisTemplate.opsForValue().get(syncedKey);
            if ("2".equals(synced)) {
                // 恢复到已同步状态
                redisTemplate.opsForValue().set(syncedKey, "1");

                // 重新恢复点赞集合和计数
                redisTemplate.opsForSet().add(likeKey, userId.toString());
                redisTemplate.opsForValue().increment(countKey, 1);

                return true; // 这次恢复是成功的
            }
            return false; // 已经是正常点赞状态
        }

        // 正常点赞流程
        redisTemplate.opsForSet().add(likeKey, userId.toString());
        redisTemplate.opsForValue().increment(countKey, 1);
        redisTemplate.opsForValue().set(syncedKey, "0");

        return true;
    }


    // 取消点赞
    public boolean unlikeArticle(Long articleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) authentication.getPrincipal();
        Long userId = loginUser.getId();
        String likeKey = LIKE_KEY_PREFIX + articleId;
        String countKey = COUNT_KEY_PREFIX + articleId;
        String syncedKey = SYNCED_KEY_PREFIX + articleId + ":" + userId;

        // 判断是否点赞过
        Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId.toString());
        if (Boolean.FALSE.equals(isMember)) {
            return false; // 从未点赞
        }

        // 取消点赞
        //这里先不删除likekey，因为如果这是文章的最后一个赞，它的set会直接删除，定时任务查找时无法找到likeKey，无法获得article_id
        //redisTemplate.opsForSet().remove(likeKey, userId.toString());
        redisTemplate.opsForValue().decrement(countKey, 1);

        // 软删除标记
        String synced = redisTemplate.opsForValue().get(syncedKey);
        if ("1".equals(synced)) {
            // 已同步的，标记为2，等定时任务批量删除
            redisTemplate.opsForValue().set(syncedKey, "2");
        } else {
            // 未同步的，直接删除标记（脏数据不写入MySQL了）
            redisTemplate.opsForSet().remove(likeKey, userId.toString());
            redisTemplate.delete(syncedKey);
        }

        return true;
    }

    public int getArticleLikeCount(Long articleId) {
        String key = COUNT_KEY_PREFIX + articleId;
        Long count = redisTemplate.opsForValue().increment(key, 0);  // 读取当前计数
        if (count != null) {
            return count.intValue();
        }
        // 如果Redis没有，说明可能是冷数据，去数据库查，并回写到Redis
        int likeCount = likeMapper.getLikeById(articleId);
        redisTemplate.opsForValue().set(key, String.valueOf(likeCount));
        return likeCount;
    }



    @Transactional
    public void syncLikesToMySQL() {
        Set<String> keys = redisTemplate.keys(LIKE_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String likeKey : keys) {
            Long articleId = Long.parseLong(likeKey.replace(LIKE_KEY_PREFIX, ""));
            Set<String> userIds = redisTemplate.opsForSet().members(likeKey);
            if (userIds == null) {
                userIds = Collections.emptySet();
            }

            // 1. 同步新点赞记录到MySQL
            for (String userId : userIds) {
                String syncedKey = SYNCED_KEY_PREFIX + articleId + ":" + userId;
                String synced = redisTemplate.opsForValue().get(syncedKey);
                if ("0".equals(synced)) {
                    likeMapper.addArticleLike(articleId, Long.parseLong(userId));
                    redisTemplate.opsForValue().set(syncedKey, "1");
                }
            }

            // 2. 处理状态2的“软删除”记录（真正删除MySQL）
            Set<String> allUsers = redisTemplate.keys(SYNCED_KEY_PREFIX + articleId + ":*");
            if (allUsers != null) {
                for (String syncedKey : allUsers) {
                    String synced = redisTemplate.opsForValue().get(syncedKey);
                    if ("2".equals(synced)) {
                        logger.info("删除点赞信息");
                        Long userId = Long.parseLong(syncedKey.substring(syncedKey.lastIndexOf(":") + 1));
                        redisTemplate.opsForSet().remove(likeKey, userId.toString());//这里再删除redis里的点赞信息，以保证找到要被删除的key
                        likeMapper.deleteArticleLike(articleId, userId);
                        redisTemplate.delete(syncedKey);
                    }
                }
            }

            // 3. 同步likeCount
            String countKey = COUNT_KEY_PREFIX + articleId;
            String likeCountStr = redisTemplate.opsForValue().get(countKey);
            if (likeCountStr != null) {
                int likeCount = Integer.parseInt(likeCountStr);
                likeMapper.updateLikeCount(articleId, likeCount);
            }
        }
    }
}
