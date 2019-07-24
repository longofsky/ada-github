package com.ada.news.provider.task;

import com.ada.news.service.contentbasedrecommend.ContentBasedRecommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: ada-github
 * @Package: com.ada.news.provider.task
 * @ClassName: ContentBasedTask
 * @Author: litianlong
 * @Description: ${description}
 * @Date: 2019-07-23 11:21
 * @Version: 1.0
 */
@Component
public class ContentBasedTask {

    Logger logger = LoggerFactory.getLogger(ContentBasedTask.class);

//    @Autowired
//    private ContentBasedRecommender contentBasedRecommender;
    /**
     *每天 凌晨1点文章分类打标定时任务
     */
    @Scheduled(cron = "0 0/10 * * * *")
    public void contentBasedTask () {

        /**
         * todo
         */
//        contentBasedRecommender.recommend(null);

    }

}
