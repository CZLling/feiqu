package com.feiqu.system.service.impl;

import com.feiqu.system.mapper.UserActionNewMapper;
import com.feiqu.system.model.UserActionNew;
import com.feiqu.system.service.UserActionNewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserActionNewServiceImpl implements UserActionNewService {

    @Autowired
    UserActionNewMapper userActionNewMapper;

    public Integer insertAction(UserActionNew userActionNew){
        return userActionNewMapper.insertAction(userActionNew);
    }

    public boolean checkActionExist(Integer actionUserId ,Integer actionType,Integer articleId) {
        if (actionUserId == null || actionType == null || articleId == null) {
            throw new NullPointerException("param is null");
        }
        int count =  userActionNewMapper.queryActionCount(actionUserId, actionType, articleId);
        return  count != 0 ? true : false;
    }


    public UserActionNew getActionByIds(Integer actionUserId ,Integer articleId){
        return userActionNewMapper.getActionByIds(actionUserId,articleId);
    }


    public void updateActionByIds(UserActionNew userAction){
        userActionNewMapper.updateActionByIds(userAction);
    }

    public List<UserActionNew> getActionByUserId(Integer actionUserId){
        return userActionNewMapper.getActionByUserId(actionUserId);
    }






}
