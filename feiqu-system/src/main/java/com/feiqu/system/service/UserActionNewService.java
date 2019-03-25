package com.feiqu.system.service;


import com.feiqu.system.model.UserActionNew;

import java.util.List;

/**

*/
public interface UserActionNewService {

     Integer insertAction(UserActionNew userActionNew);

     boolean checkActionExist(Integer actionUserId ,Integer actionType,Integer articleId);

     UserActionNew getActionByIds(Integer actionUserId ,Integer articleId);

     void updateActionByIds(UserActionNew userAction);

     List<UserActionNew> getActionByUserId(Integer actionUserId);








}