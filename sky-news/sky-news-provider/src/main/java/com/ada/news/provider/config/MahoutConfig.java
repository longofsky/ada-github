package com.ada.news.provider.config;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLBooleanPrefJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * @ProjectName: ada-github
 * @Package: com.ada.news.provider.config
 * @ClassName: MahoutConfig
 * @Author: litianlong
 * @Description: ${description}
 * @Date: 2019-07-23 14:25
 * @Version: 1.0
 */
@Configuration
public class MahoutConfig {


    @Autowired
    private ApplicationContext context;

    //偏好表表名
    public static final String PREF_TABLE="news_logs";
    //用户id列名
    public static final String PREF_TABLE_USERID="user_id";
    //新闻id列名
    public static final String PREF_TABLE_NEWSID="news_id";
    //偏好值列名
    public static final String PREF_TABLE_PREFVALUE="prefer_degree";
    //用户浏览时间列名
    public static final String PREF_TABLE_TIME="view_time";

    private DataSource getDataSource(){
        return context.getBean(DataSource.class);
    }

    @Bean
    public DataModel getMySQLJDBCDataModel(){
        return new MySQLBooleanPrefJDBCDataModel(getDataSource(), PREF_TABLE, PREF_TABLE_USERID,
                PREF_TABLE_NEWSID,PREF_TABLE_TIME);
    }
}
