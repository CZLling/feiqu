package com.feiqu.system.service.impl;

import com.feiqu.system.mapper.UserActionNewMapper;
import com.feiqu.system.model.UserActionNew;
import com.feiqu.system.service.UserActionNewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserActionNewServiceImpl implements UserActionNewService {

    @Autowired
    UserActionNewMapper userActionNewMapper;


    public Integer insertCollect(UserActionNew userActionNew){
        return userActionNewMapper.insertCollect(userActionNew);
    }

    public boolean checkActionExist(Integer actionUserId ,Integer actionType,Integer articleId) {
        if (actionUserId == null || actionType == null || articleId == null) {
            throw new NullPointerException("param is null");
        }
        int count =  userActionNewMapper.queryActionCount(actionUserId, actionType, articleId);
        return  count != 0 ? true : false;
    }

    public Integer insertLike(UserActionNew userActionNew){
        return userActionNewMapper.insertLike(userActionNew);
    }

    public Integer insertFollow(UserActionNew userActionNew){
        return userActionNewMapper.insertFollow(userActionNew);
    }

    public boolean checkFollowActionExist(Integer actionUserId ,Integer actionType,Integer otherUserId) {
        if (actionUserId == null || actionType == null || otherUserId == null) {
            throw new NullPointerException("param is null");
        }
        return userActionNewMapper.queryFollowActionCount(actionUserId, actionType, otherUserId) !=0 ? true : false;
    }

    public Integer insertBrowse(UserActionNew userActionNew){
        return userActionNewMapper.insertBrowse(userActionNew);
    }

}
