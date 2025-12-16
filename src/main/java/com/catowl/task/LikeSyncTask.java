package com.catowl.task;

import com.catowl.exception.GlobalExceptionHandler;
import com.catowl.service.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LikeSyncTask {

    @Autowired
    private LikeService likeService;

    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Scheduled(fixedRate = 60000)// 每1分钟同步一次
    public void syncLikesToMySQL() {
        logger.info("定时同步开始");
        likeService.syncLikesToMySQL();
    }
}