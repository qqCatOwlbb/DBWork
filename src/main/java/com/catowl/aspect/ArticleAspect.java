package com.catowl.aspect;

import com.catowl.service.ArticleService;
import com.catowl.utils.RedisCache;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ArticleAspect {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RedisCache redisCache;

    @Pointcut("execution(public * com.catowl.controller.ArticleController.getById(..))")
    public void myPointCut(){}

    @After("myPointCut()")
    public void doAfter(JoinPoint joinPoint)throws Exception{
        // 拿到目标方法的所有参数
        Object[] objs=joinPoint.getArgs();
        // 取第一个参数
        Long id=(Long) objs[0];
        redisCache.zIncrementScore("viewNum",id.toString(),1);
    }
}
