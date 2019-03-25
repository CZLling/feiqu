package com.feiqu.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.feiqu.system.model.Article;
import com.feiqu.system.service.ArticleService;
import com.feiqu.system.service.RecommenderService;
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

    @Override
    public List<Article> userBasedRecommender(int userID, int size) throws TasteException {
        int NEIGHBORHOOD_NUM = 2;
        UserSimilarity similarity  = new PearsonCorrelationSimilarity(dataModel);
        UserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, similarity, dataModel );
        Recommender recommender =new CachingRecommender( new GenericUserBasedRecommender(dataModel , neighbor, similarity));
        List<RecommendedItem> recommendations = recommender.recommend(userID, size);
        List<Integer> articleId = Lists.newArrayList();
        for (RecommendedItem recommendedItem : recommendations){
            articleId.add(Integer.valueOf(String.valueOf(recommendedItem.getItemID())));
        }
        _log.info(">>>>>>>>>>>>>>>>"+ JSON.toJSONString(recommendations));
        // todo 根据articleId集合查询笔记
//        articleService.
        return null;
    }

    @Override
    public List<Article> myItemBasedRecommender(int userID, int size) throws TasteException {
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
        Recommender   recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
        List<RecommendedItem> recommendations = recommender.recommend(userID,size);
        List<Integer> articleId = Lists.newArrayList();
        for (RecommendedItem recommendedItem : recommendations){
            articleId.add(Integer.valueOf(String.valueOf(recommendedItem.getItemID())));
        }
        _log.info(">>>>>>>>>>>>>>>>"+ JSON.toJSONString(recommendations));
        return null;
    }

    @Override
    public List<Article> itemBasedRecommender(int userID, int article, int size) throws TasteException {
        List<Long> recommendItems = new ArrayList<>();
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
        GenericItemBasedRecommender  recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
        List<RecommendedItem> recommendations = recommender.recommendedBecause(userID,article,size);
        List<Integer> articleId = Lists.newArrayList();
        for (RecommendedItem recommendedItem : recommendations){
            articleId.add(Integer.valueOf(String.valueOf(recommendedItem.getItemID())));
        }
        _log.info(">>>>>>>>>>>>>>>>"+ JSON.toJSONString(recommendations));
        return null;
    }
}
