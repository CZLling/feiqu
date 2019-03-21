package com.feiqu.framwork.aspectj;

import com.feiqu.common.annotation.UserAction;
import com.feiqu.common.enums.UserActionEnum;
import com.feiqu.framwork.util.WebUtil;
import com.feiqu.system.model.Article;
import com.feiqu.system.model.UserActionNew;
import com.feiqu.system.pojo.cache.FqUserCache;
import com.feiqu.system.service.ArticleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Order(1)
@Component
public class UserActionAspect {
    protected static Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);
    @Autowired
    private ArticleService articleService;
    @Autowired
    private WebUtil webUtil;


    @Pointcut("@annotation(com.feiqu.common.annotation.UserAction)")
    public void annotationPoinCut() {
    }


    @After("annotationPoinCut()")
    public void after(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] objects = joinPoint.getArgs();
        Method method = signature.getMethod();
        UserAction action = method.getAnnotation(UserAction.class);
        insertUserAction(objects,action);
    }


    private void insertUserAction(Object[] objects,UserAction action){
        if(action.actionType() == UserActionEnum.COLLECT.getValue()){
            //todo insert
//            String userid = String.valueOf(objects[0]);
            Integer aid = Integer.valueOf(String.valueOf(objects[1]));
            HttpServletRequest request = (HttpServletRequest) objects[2];
            HttpServletResponse response = (HttpServletResponse) objects[3];
            FqUserCache fqUser = webUtil.currentUser(request,response);
            Article articleDB = articleService.selectByPrimaryKey(aid);
            UserActionNew userAction = new UserActionNew();
            userAction.setActionType(UserActionEnum.COLLECT.getValue());
            userAction.setActionUserId(fqUser.getId());
            userAction.setArticleId(aid);
            userAction.setArticleLabel(articleDB.getLabel());
            userAction.setOtherUserId(articleDB.getUserId());
            userAction.setCreateTime(new Date());


            if(fqUser.getId() != articleDB.getUserId()){
                // todo  1.dao -> xml -> service   insert() >> userAction
            }

            logger.info("2222222222222"+articleDB.getUserId() + articleDB.getLabel()+fqUser.getId());
        }
    }




}
