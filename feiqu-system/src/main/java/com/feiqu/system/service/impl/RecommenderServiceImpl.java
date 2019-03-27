package com.feiqu.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.feiqu.common.enums.CalRecommendEnum;
import com.feiqu.common.enums.YesNoEnum;
import com.feiqu.system.model.Article;
import com.feiqu.system.model.ArticleExample;
import com.feiqu.system.model.UserActionNew;
import com.feiqu.system.service.ArticleService;
import com.feiqu.system.service.RecommenderService;
import com.feiqu.system.service.UserActionNewService;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommenderServiceImpl implements RecommenderService {
    private static Logger _log = LoggerFactory.getLogger(RecommenderServiceImpl.class);

    @Autowired
    private DataModel dataModel;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserActionNewService userActionNewService;

    @Autowired
    RecommenderService recommenderService;

    @Override
    public List<Article> userBasedRecommender(int userID, int size) throws TasteException {
        UserSimilarity similarity  = new PearsonCorrelationSimilarity(dataModel);
        UserNeighborhood neighbor = new NearestNUserNeighborhood(CalRecommendEnum.NEIGHBORHOOD_NUM.getValue(), similarity, dataModel );
        Recommender recommender =new CachingRecommender( new GenericUserBasedRecommender(dataModel , neighbor, similarity));
        List<RecommendedItem> recommendations = recommender.recommend(userID, size);
        if(CollectionUtil.isNotEmpty(recommendations)) {
            List<Integer> articleId = Lists.newArrayList();
            for (RecommendedItem recommendedItem : recommendations) {
                articleId.add(Integer.valueOf(String.valueOf(recommendedItem.getItemID())));
            }
            List<Article> recommendArticles = articleService.getArticleByIds(articleId, userID);
            return recommendArticles;
        }
        return null;
    }

    @Override
    public List<Article> myItemBasedRecommender(int userID,int size) throws TasteException {
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
        Recommender   recommender = new CachingRecommender(new GenericItemBasedRecommender(dataModel, itemSimilarity));
        List<RecommendedItem> recommendations = recommender.recommend(userID,size);
        if(CollectionUtil.isNotEmpty(recommendations)) {
            List<Integer> articleId = Lists.newArrayList();
            for (RecommendedItem recommendedItem : recommendations) {
                articleId.add(Integer.valueOf(String.valueOf(recommendedItem.getItemID())));
            }
            List<Article> recommendArticles = articleService.getArticleByIds(articleId, userID);
            return recommendArticles;
        }
        return null;
    }

    @Override
    public List<Article> itemBasedRecommender(int userID, int articleId, int size) throws TasteException {
        List<Long> recommendItems = new ArrayList<>();
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
        List<RecommendedItem> recommendations = recommender.recommendedBecause(userID, articleId, size);
        //基于单篇文章的自动推荐，为空就同之前逻辑
        if (CollectionUtil.isEmpty(recommendations)) {
            Article article = articleService.selectByPrimaryKey(articleId);
            ArticleExample articleExample = new ArticleExample();
            articleExample.createCriteria().andLabelEqualTo(article.getLabel()).andDelFlagEqualTo(YesNoEnum.NO.getValue());
            articleExample.setOrderByClause("browse_count desc");
            PageHelper.startPage(1, 10, false);
            List<Article> articles = articleService.selectByExample(articleExample);
            articles = articles.stream().filter(e -> !e.getId().equals(article.getId())).collect(Collectors.toList());
            return articles;
        }
        List<Integer> articleIds = Lists.newArrayList();
        for (RecommendedItem recommendedItem : recommendations) {
            articleIds.add(Integer.valueOf(String.valueOf(recommendedItem.getItemID())));
        }
        List<Article> recommendArticles = articleService.getArticleByIds(articleIds, userID);
        return recommendArticles;
    }




    @Override
    public List<Article> selfBuiltRecommendation(Integer actionUserId, Integer recommendType) throws Exception {
        List<UserActionNew> actionlist = userActionNewService.getActionByUserId(actionUserId);
        List<Article> recommendArticles;
        //管理员推荐笔记，user_action为空-->管理员推荐笔记
        if (CollectionUtil.isEmpty(actionlist)) {
            ArticleExample example = new ArticleExample();
            example.setOrderByClause("browse_count desc");
            example.createCriteria().andIsRecommendEqualTo(YesNoEnum.YES.getValue());
            recommendArticles = articleService.selectByExample(example);
            return recommendArticles;
        }
        //基于用户推荐和基于协同过滤算法推荐，user_action不为空-->自动推荐笔记
        List<Article> autoRecommendArticles = Lists.newArrayList();
        if(recommendType == CalRecommendEnum.RECOMMEND_USER.getValue()){
            autoRecommendArticles = userBasedRecommender(actionUserId,CalRecommendEnum.RECOMMEND_COUNT.getValue());
        }else if(recommendType == CalRecommendEnum.RECOMMEND_ARTICLE.getValue()){
            autoRecommendArticles = myItemBasedRecommender(actionUserId,CalRecommendEnum.RECOMMEND_COUNT.getValue());
        }
        //如果自动推荐结果不为空就返回该推荐结果，不足5条就按用户行为的同标签笔记前几条补足
        if (CollectionUtil.isNotEmpty(autoRecommendArticles)) {
            int recommendCount = autoRecommendArticles.size();
            if(recommendCount < CalRecommendEnum.RECOMMEND_COUNT.getValue()) {
                List<Integer> labels = Lists.newArrayList();
                actionlist.forEach(userActionNew -> labels.add(userActionNew.getArticleLabel()));
                recommendArticles = articleService.getArticleByLabels(labels, actionUserId,CalRecommendEnum.RECOMMEND_COUNT.getValue()-recommendCount);
                for (Article article : recommendArticles){
                    if(!autoRecommendArticles.contains(article)) {
                        autoRecommendArticles.add(article);
                    }
                }
                return autoRecommendArticles;
            }
            return autoRecommendArticles;
        }
        //根据用户行为的同标签笔记推荐，user_action不为空且自动推荐结果为空-->笔记偏好度前两篇笔记的标签的浏览量前五笔记
            List<Integer> labels = Lists.newArrayList();
            actionlist.forEach(userActionNew -> labels.add(userActionNew.getArticleLabel()));
            recommendArticles = articleService.getArticleByLabels(labels, actionUserId,CalRecommendEnum.RECOMMEND_COUNT.getValue());
            return recommendArticles;

    }



}

