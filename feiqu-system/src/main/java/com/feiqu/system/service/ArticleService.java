package com.feiqu.system.service;


import com.feiqu.system.base.BaseService;
import com.feiqu.system.model.Article;
import com.feiqu.system.model.ArticleExample;
import com.feiqu.system.pojo.response.ArticleUserDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* ArticleService接口
* created by cwd on 2017/9/29.
*/
public interface ArticleService extends BaseService<Article, ArticleExample> {
    List<ArticleUserDetail> selectUserByExampleWithBLOBs(ArticleExample example);

    List<Article> getArticleByLabels(List<Integer> labels, Integer userId,Integer count);

    List<Article> getArticleByIds(List<Integer> ids, Integer userId);

}