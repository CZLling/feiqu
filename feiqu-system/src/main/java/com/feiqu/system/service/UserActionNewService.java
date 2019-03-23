package com.feiqu.system.service;


import com.feiqu.system.model.UserActionNew;

/**

*/
public interface UserActionNewService {

     Integer insertCollect(UserActionNew userActionNew);

     boolean checkActionExist(Integer actionUserId ,Integer actionType,Integer articleId);

     Integer insertLike(UserActionNew userActionNew);

     Integer insertFollow(UserActionNew userActionNew);

     boolean checkFollowActionExist(Integer actionUserId ,Integer actionType,Integer otherUserId);

     Integer insertBrowse(UserActionNew userActionNew);



}