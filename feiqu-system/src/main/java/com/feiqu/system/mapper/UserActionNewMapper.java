package com.feiqu.system.mapper;


import com.feiqu.system.model.UserActionNew;
import org.apache.ibatis.annotations.Param;

public interface UserActionNewMapper {

     Integer insertCollect(UserActionNew userActionNew);

     Integer queryActionCount(@Param("actionUserId") Integer actionUserId ,@Param("actionType") Integer actionType,@Param("articleId") Integer articleId);

     Integer insertLike(UserActionNew userActionNew);

     Integer insertFollow(UserActionNew userActionNew);

     Integer queryFollowActionCount(@Param("actionUserId") Integer actionUserId ,@Param("actionType") Integer actionType,@Param("otherUserId") Integer otherUserId);

     Integer insertBrowse(UserActionNew userActionNew);

}