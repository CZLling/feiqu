package com.feiqu.web;


import com.alibaba.fastjson.JSON;
import com.feiqu.FeiQuApplication;
import com.feiqu.system.model.Article;
import com.feiqu.system.service.RecommenderService;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)     //表示继承了SpringJUnit4ClassRunner类
@SpringBootTest
public class RecommandTest {

    private static final Logger loger = LoggerFactory.getLogger(RecommandTest.class);


    @Autowired
    RecommenderService recommenderService;


    @Test
    public void  userBasedRecommender(){
      int userID = 3 ;
      int size = 5;
      try {
          List<RecommendedItem> recommendations = recommenderService.userBasedRecommender(userID, size);
      }catch (Exception e){
          loger.error("error",e);
      }
    }



    @Test
    public void myItemBasedRecommender(){
        int userID = 3 ;
        int size = 2;
        try {
            List<RecommendedItem> recommendations = recommenderService.myItemBasedRecommender(userID, size);
        }catch (Exception e){
            loger.error("error",e);

        }
    }



    @Test
    public void  itemBasedRecommender(){
      int userID = 2 ;
      int size = 5;
      int article = 22;
      try {
          List<RecommendedItem> recommendations = recommenderService.itemBasedRecommender(userID,article, size);
      }catch (Exception e){
          loger.error("error",e);

      }
    }


    @Test
    public void FinallyRecommend() {
        int userID = 2;
        int article = 22;
        try {
            List<Article> recommendArticles = recommenderService. AutoRecommend(userID, article);
        } catch (Exception e) {
            loger.error("error", e);
        }

    }




}
