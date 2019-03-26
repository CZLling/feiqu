package com.feiqu.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.feiqu.common.enums.YesNoEnum;
import com.feiqu.system.model.Article;
import com.feiqu.system.model.ArticleExample;
import com.feiqu.system.model.UserActionNew;
import com.feiqu.system.service.ArticleService;
import com.feiqu.system.service.RecommenderService;
import com.feiqu.system.service.UserActionNewService;
import com.google.common.collect.Lists;
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
    public List<RecommendedItem> userBasedRecommender(int userID, int size) throws TasteException {
        int NEIGHBORHOOD_NUM = 2;
        UserSimilarity similarity  = new PearsonCorrelationSimilarity(dataModel);
        UserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, similarity, dataModel );
        Recommender recommender =new CachingRecommender( new GenericUserBasedRecommender(dataModel , neighbor, similarity));
        List<RecommendedItem> recommendations = recommender.recommend(userID, size);
        return recommendations;
    }

    @Override
    public List<RecommendedItem> myItemBasedRecommender(int userID, int size) throws TasteException {
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
        Recommender   recommender = new CachingRecommender(new GenericItemBasedRecommender(dataModel, itemSimilarity));
        List<RecommendedItem> recommendations = recommender.recommend(userID,size);
        return recommendations;

    }

    @Override
    public List<RecommendedItem> itemBasedRecommender(int userID, int article, int size) throws TasteException {
        List<Long> recommendItems = new ArrayList<>();
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
        GenericItemBasedRecommender  recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
        List<RecommendedItem> recommendations = recommender.recommendedBecause(userID,article,size);
        return recommendations;
    }

    @Override
    public List<Article>  AutoRecommend(int actionUserId,int article) throws TasteException {
        int size = 5;

            List<RecommendedItem> userBasedRecommend = recommenderService.userBasedRecommender(actionUserId, size);
            List<RecommendedItem> myItemBasedRecommend = recommenderService.myItemBasedRecommender(actionUserId, size);
            userBasedRecommend.addAll(myItemBasedRecommend);
        List<Integer> articleId = Lists.newArrayList();
        for (RecommendedItem recommendedItem : userBasedRecommend){
            if(!articleId.contains(recommendedItem)){
                articleId.add(Integer.valueOf(String.valueOf(recommendedItem.getItemID())));
            }
        }
        _log.info(">>>>>>>>>>>>>>>>"+ JSON.toJSONString(articleId));
        List<Article> recommendArticles = articleService.getArticleByIds(articleId,actionUserId);
        _log.info(">>>>>>>>>>>>>>>>"+ JSON.toJSONString(recommendArticles));
        return recommendArticles;
    }


    @Override
    public List<Article> SelfBuiltRecommendation(int actionUserId, int article) throws Exception {
        List<UserActionNew> actionlist = userActionNewService.getActionByUserId(actionUserId);
        List<Article> recommendArticles;//推荐笔记,user_action不为空-->前两个标签的五篇笔记，为空-->管理员推荐笔记
        //
        if (CollectionUtil.isEmpty(actionlist)) {
            ArticleExample example = new ArticleExample();
            example.setOrderByClause("browse_count desc");
            example.createCriteria().andIsRecommendEqualTo(YesNoEnum.YES.getValue());
            recommendArticles = articleService.selectByExample(example);
            return recommendArticles;
        }
        //
//        List<Article> autoRecommendArticles = AutoRecommend(actionUserId, article);
//        if (CollectionUtil.isNotEmpty(autoRecommendArticles)) {
//            return autoRecommendArticles;
//        }
        //
        List<Integer> labels = Lists.newArrayList();
        actionlist.forEach(userActionNew -> labels.add(userActionNew.getArticleLabel()));
        recommendArticles = articleService.getArticleByLabels(labels, actionUserId);
        return recommendArticles;
    }



}

