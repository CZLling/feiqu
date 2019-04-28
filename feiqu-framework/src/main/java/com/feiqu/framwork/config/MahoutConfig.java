package com.feiqu.framwork.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MahoutConfig {

    @Autowired
    private DataSource masterDataSource;

//    private MysqlDataSource getDataSource(){
//        MysqlDataSource dataSource=new MysqlDataSource();
//        dataSource.setServerName("localhost");
//        dataSource.setUser("root");
//        dataSource.setPassword("password");
//        dataSource.setDatabaseName("cwd_boring");
//        return dataSource;
//    }

    @Bean(autowire = Autowire.BY_NAME,value = "mySQLDataModel")
    public DataModel getMySQLJDBCDataModel(){
        DataModel dataModel=new MySQLJDBCDataModel(masterDataSource,"user_action","action_user_id","article_id","action_type", "create_time");
        return dataModel;
    }

}