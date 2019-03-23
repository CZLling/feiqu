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
        insertUserAction(objects,action);
    }


    private void insertUserAction(Object[] objects,UserAction action) {
        if (action.actionType() == UserActionEnum.BROWSE.getValue()) {
            Integer aid = Integer.valueOf(String.valueOf(objects[0]));//笔记ID
            HttpServletRequest request = (HttpServletRequest) objects[1];
            HttpServletResponse response = (HttpServletResponse) objects[2];
            FqUserCache fqUser = webUtil.currentUser(request, response); //当前用户
            Article articleDB = articleService.selectByPrimaryKey(aid); //笔记信息
            UserActionNew userAction = new UserActionNew();
            userAction.setActionType(UserActionEnum.BROWSE.getValue());
            userAction.setActionUserId(fqUser.getId());
            userAction.setArticleId(aid);
            userAction.setArticleLabel(articleDB.getLabel());
            userAction.setOtherUserId(articleDB.getUserId());
            userAction.setCreateTime(new Date());
            if (fqUser.getId() != articleDB.getUserId()) {
                userActionNewService.insertBrowse(userAction);
            }
        }


        if (action.actionType() == UserActionEnum.COLLECT.getValue()) {
            //todo insert
            Integer aid = Integer.valueOf(String.valueOf(objects[1]));//笔记ID
            HttpServletRequest request = (HttpServletRequest) objects[2];
            HttpServletResponse response = (HttpServletResponse) objects[3];
            FqUserCache fqUser = webUtil.currentUser(request, response); //当前用户
            Article articleDB = articleService.selectByPrimaryKey(aid); //笔记信息
            UserActionNew userAction = new UserActionNew();
            userAction.setActionType(UserActionEnum.COLLECT.getValue());
            userAction.setActionUserId(fqUser.getId());
            userAction.setArticleId(aid);
            userAction.setArticleLabel(articleDB.getLabel());
            userAction.setOtherUserId(articleDB.getUserId());
            userAction.setCreateTime(new Date());
            // todo  1.dao -> xml -> service   insert() >> userAction
            if (fqUser.getId() != articleDB.getUserId()) {
                if (!userActionNewService.checkActionExist(fqUser.getId(),UserActionEnum.COLLECT.getValue(),aid)) {
                userActionNewService.insertCollect(userAction);
                }
            }
        }


        if (action.actionType() == UserActionEnum.LIKE.getValue()) {
            Integer aid = Integer.valueOf(String.valueOf(objects[0]));//笔记ID
            HttpServletRequest request = (HttpServletRequest) objects[1];
            HttpServletResponse response = (HttpServletResponse) objects[2];
            FqUserCache fqUser = webUtil.currentUser(request, response); //当前用户
            Article articleDB = articleService.selectByPrimaryKey(aid); //笔记信息
            UserActionNew userAction = new UserActionNew();
            userAction.setActionType(UserActionEnum.LIKE.getValue());
            userAction.setActionUserId(fqUser.getId());
            userAction.setArticleId(aid);
            userAction.setArticleLabel(articleDB.getLabel());
            userAction.setOtherUserId(articleDB.getUserId());
            userAction.setCreateTime(new Date());
            boolean a = userActionNewService.checkActionExist(fqUser.getId(),UserActionEnum.LIKE.getValue(),aid);
            if (fqUser.getId() != articleDB.getUserId()) {
               if (!userActionNewService.checkActionExist(fqUser.getId(),UserActionEnum.LIKE.getValue(),aid)) {
                userActionNewService.insertLike(userAction);
                }
            }
        }


        if (action.actionType() == UserActionEnum.FOLLOW.getValue()) {
            Integer otherUserId = Integer.valueOf(String.valueOf(objects[0]));//被关注人ID
            HttpServletRequest request = (HttpServletRequest) objects[1];
            HttpServletResponse response = (HttpServletResponse) objects[2];
            FqUserCache fqUser= webUtil.currentUser(request, response); //当前用户
            UserActionNew userAction = new UserActionNew();
            userAction.setActionType(UserActionEnum.FOLLOW.getValue());
            userAction.setActionUserId(fqUser.getId());
            userAction.setOtherUserId(otherUserId);
            userAction.setCreateTime(new Date());
            boolean a = userActionNewService.checkFollowActionExist(fqUser.getId(),UserActionEnum.FOLLOW.getValue(),otherUserId);
            if(!userActionNewService.checkFollowActionExist(fqUser.getId(),UserActionEnum.FOLLOW.getValue(),otherUserId)){
                userActionNewService.insertFollow(userAction);
            }
        }





        }


    }
