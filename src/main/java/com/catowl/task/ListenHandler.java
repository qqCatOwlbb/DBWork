package com.catowl.task;

import com.catowl.entity.Article;
import com.catowl.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate; // 引入 StringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ListenHandler {
    @Autowired
    private ArticleService articleService;

    // 移除 RedisCache，改用 StringRedisTemplate
    // 这样可以避免 SerializationException (把 "3" 当成 User 对象解析的错误)
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String VIEW_KEY = "viewNum";

    /*
     * 关闭时，将redis的数据同步到数据库中
     * */
    @EventListener(ContextClosedEvent.class)
    public void afterDestroy() {
        log.info("服务即将结束，数据开始同步");
        // 使用 stringRedisTemplate 读取，只会把数据当做字符串，不会报错
        Set<ZSetOperations.TypedTuple<String>> viewNum = stringRedisTemplate.opsForZSet().reverseRangeWithScores(VIEW_KEY, 0, -1);
        writeNum(viewNum);
        log.info("redis写入mysql");
    }

    /*
     * 定时同步
     * */
    @Scheduled(fixedRate = 60000)
    public void updateNum() {
        log.info("同步点击量");
        Set<ZSetOperations.TypedTuple<String>> viewNum = stringRedisTemplate.opsForZSet().reverseRangeWithScores(VIEW_KEY, 0, -1);
        writeNum(viewNum);
    }

    /*
     * 用于将redis中获取的set同步到mysql中
     * */
    private void writeNum(Set<ZSetOperations.TypedTuple<String>> set) {
        if (set == null || set.isEmpty()) {
            return;
        }
        set.forEach(item -> {
            try {
                String rawValue = item.getValue();
                // 核心修复：去除可能存在的首尾双引号
                // 原因：之前的 JSON 序列化可能将 ID "1" 存为了 "\"1\""
                if (rawValue != null && rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
                    rawValue = rawValue.substring(1, rawValue.length() - 1);
                }

                Long id = Long.valueOf(rawValue);
                Integer num = item.getScore().intValue();

                Article article = new Article();
                article.setId(id);
                article.setView_count(num);
                articleService.updateNumById(article);

                // 删除 Redis 中对应的 Key (使用原始值删除，因为 Key 在 Redis 里就是带引号的)
                stringRedisTemplate.opsForZSet().remove(VIEW_KEY, item.getValue());
                log.info("更新浏览量: 文章ID={} 浏览量={}", id, num);
            } catch (Exception e) {
                // 打印更详细的错误日志，方便排查具体是哪个值出了问题
                log.error("同步文章浏览量失败: 原始值=[{}]", item.getValue(), e);
            }
        });
    }
}