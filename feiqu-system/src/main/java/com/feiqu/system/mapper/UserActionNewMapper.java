package com.feiqu.system.mapper;


import com.feiqu.system.model.UserActionNew;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserActionNewMapper {

     Integer insertAction(UserActionNew userActionNew);

     Integer queryActionCount(@Param("actionUserId") Integer actionUserId ,@Param("actionType") Integer actionType,@Param("articleId") Integer articleId);

     UserActionNew getActionByIds(@Param("actionUserId") Integer actionUserId ,@Param("articleId") Integer articleId);

     void updateActionByIds(UserActionNew userAction);

     List<UserActionNew> getActionByUserId(@Param("actionUserId") Integer actionUserId);









}