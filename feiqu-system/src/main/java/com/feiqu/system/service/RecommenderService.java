package com.feiqu.system.service;

import com.feiqu.system.model.Article;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import java.util.List;

public interface RecommenderService {

    List<RecommendedItem>  userBasedRecommender(int userID, int size) throws TasteException;

    List<RecommendedItem>  myItemBasedRecommender(int userID,int size) throws TasteException;

    List<RecommendedItem>  itemBasedRecommender(int userID,int article,int size) throws TasteException;

    List<Article> AutoRecommend(int actionUserId,int article)throws TasteException;

    List<Article>  SelfBuiltRecommendation(int actionUserId,int article) throws Exception;


}
