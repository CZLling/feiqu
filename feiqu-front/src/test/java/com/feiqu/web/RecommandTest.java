package com.feiqu.web;


import com.feiqu.FeiQuApplication;
import com.feiqu.system.model.Article;
import com.feiqu.system.service.RecommenderService;
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
      int userID = 2 ;
      int size = 5;
      try {
          List<Article> articles = recommenderService.userBasedRecommender(userID, size);
          List<Article> articless = recommenderService.myItemBasedRecommender(userID, size);
      }catch (Exception e){
          loger.error("error",e);

      }

    }

    @Test
    public void  itemBasedRecommender(){
      int userID = 2 ;
      int size = 1;
      try {
          List<Article> articless = recommenderService.itemBasedRecommender(userID,11, size);
      }catch (Exception e){
          loger.error("error",e);

      }

    }


}
