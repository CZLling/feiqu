package com.feiqu.framwork.aspectj;

import com.feiqu.common.annotation.UserAction;
import com.feiqu.common.enums.UserActionEnum;
import com.feiqu.framwork.util.WebUtil;
import com.feiqu.system.model.Article;
import com.feiqu.system.model.UserActionNew;
import com.feiqu.system.pojo.cache.FqUserCache;
import com.feiqu.system.service.ArticleService;
import com.feiqu.system.service.UserActionNewService;
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
import java.util.*;

@Aspect
@Order(1)
@Component
public class UserActionAspect {
    protected static Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);
    @Autowired
    private ArticleService articleService;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    UserActionNewService userActionNewService;



    @Pointcut("@annotation(com.feiqu.common.annotation.UserAction)")
    public void annotationPoinCut() {
    }


    @After("annotationPoinCut()")
    public void after(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] objects = joinPoint.getArgs();
        Method method = signature.getMethod();
        UserAction action = method.getAnnotation(UserAction.class);
        if(objects!=null && !Objects.isNull(action))
        insertUserAction(objects,action);
    }


    private void insertUserAction(Object[] objects,UserAction action) {
        Integer aid = Integer.valueOf(String.valueOf(objects[0]));//笔记ID
        HttpServletRequest request = (HttpServletRequest) objects[1];
        HttpServletResponse response = (HttpServletResponse) objects[2];
        FqUserCache fqUser = webUtil.currentUser(request, response); //当前用户
        Article articleDB = articleService.selectByPrimaryKey(aid); //笔记信息

        if (action.actionType() == UserActionEnum.BROWSE.getValue()) {
            insertOrUpdateAction(aid, fqUser, articleDB,action.actionType());
 
        }
        if (action.actionType() == UserActionEnum.LIKE.getValue()) {
            insertOrUpdateAction(aid, fqUser, articleDB,action.actionType());
        }
        if (action.actionType() == UserActionEnum.COLLECT.getValue()) {
            String type = String.valueOf(objects[3]);//add or remove
            if("add".equals(type)){
                insertOrUpdateAction(aid, fqUser, articleDB,action.actionType());
            }else if("remove".equals(type)){
                insertOrUpdateAction(aid, fqUser, articleDB,UserActionEnum.CANCELCOLLECT.getValue());
            }
        }
    }

    private UserActionNew packUserAction(Integer aid, FqUserCache fqUser, Article articleDB,int actionType){
        UserActionNew userAction = new UserActionNew();
        userAction.setActionType(Integer.valueOf(actionType));
        userAction.setActionUserId(fqUser.getId());
        userAction.setArticleId(aid);
        userAction.setArticleLabel(articleDB.getLabel());
        userAction.setOtherUserId(articleDB.getUserId());
        userAction.setCreateTime(new Date());
        return userAction;
    }

    private void insertOrUpdateAction(Integer aid, FqUserCache fqUser, Article articleDB,int actionType) {
        UserActionNew userAction =  packUserAction(aid,fqUser,articleDB, actionType);
        if (fqUser.getId() != articleDB.getUserId()) {
            UserActionNew userActionNew = userActionNewService.getActionByIds(fqUser.getId(),aid);
            if(Objects.isNull(userActionNew)){
                userActionNewService.insertAction(userAction);
            }else{
                userAction.setActionType(Integer.valueOf(userActionNew.getActionType()+ actionType));
                userActionNewService.updateActionByIds(userAction);
            }
        }

    }



}
