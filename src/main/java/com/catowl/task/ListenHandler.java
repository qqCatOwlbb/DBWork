package com.catowl.task;

import com.catowl.entity.Article;
import com.catowl.service.ArticleService;
import com.catowl.utils.RedisCache;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ListenHandler {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RedisCache redisCache;

    private  static final String VIEW_KEY="viewNum";

    /*
    * 关闭时，将redis的数据同步到数据库中
    * */
    //@PreDestroy 这个注解可能会在redis关闭后才启动，此时读取会发生错误
    @EventListener(ContextClosedEvent.class)
    public void afterDestroy(){
        log.info("服务即将结束，数据开始同步");
        //将redis的点击量同步到mysql
        //Set<ZSetOperations.TypedTuple<String>> viewNum 表示一个 Redis 有序集合成员的集合，
        // 每个成员都有值（String 类型）和分数（Double 类型），
        // 通常用于排行榜等场景。
        Set<ZSetOperations.TypedTuple<String>> viewNum = redisCache.zReverseRangeWithScores("viewNum",0,-1);
        writeNum(viewNum);
        log.info("redis写入mysql");
    }

    /*
    * 定时同步
    * */
    @Scheduled(fixedRate = 60000)
    public void updateNum(){
      log.info("同步点击量");
      Set<ZSetOperations.TypedTuple<String>> viewNum=redisCache.zReverseRangeWithScores("viewNum",0,-1);
      writeNum(viewNum);
    }

    /*
    * 用于将redis中获取的set同步到mysql中
    * */
    private void writeNum(Set<ZSetOperations.TypedTuple<String>> set){
        set.forEach(item->{
            Long id = Long.valueOf(item.getValue());
            Integer num = item.getScore().intValue(); //redis中数据可能为null
            Article article = new Article();
            article.setId(id);
            article.setView_count(num);
            articleService.updateNumById(article);
            redisCache.delZset(VIEW_KEY,id.toString());
            log.info("更新浏览量");
        });
    }
}
