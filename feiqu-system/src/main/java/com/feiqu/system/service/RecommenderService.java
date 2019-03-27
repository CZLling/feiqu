package com.feiqu.system.service;

import com.feiqu.system.model.Article;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import java.util.List;

public interface RecommenderService {

    List<Article>  userBasedRecommender(int userID, int size) throws TasteException;

    List<Article>  myItemBasedRecommender(int userID, int size) throws TasteException;

    List<Article>  itemBasedRecommender(int userID, int articleId, int size) throws TasteException;

    List<Article>  selfBuiltRecommendation(Integer actionUserId, Integer recommendType) throws Exception;


}
