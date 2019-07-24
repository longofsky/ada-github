package com.ada.news.provider.controller;

import com.ada.common.util.AdaResponse;
import com.ada.news.service.UserBasedCollaborativeRecommender.MahoutUserBasedCollaborativeRecommender;
import com.ada.news.service.contentbasedrecommend.ContentBasedRecommender;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: ada-github
 * @Package: com.ada.news.provider.controller
 * @ClassName: NewsRecommendController
 * @Author: litianlong
 * @Description: ${description}
 * @Date: 2019-07-24 10:41
 * @Version: 1.0
 */

@RestController
@RequestMapping("/news")
public class NewsRecommendController {

    Logger logger = LoggerFactory.getLogger(NewsScraperController.class);

    @Autowired
    private ContentBasedRecommender contentBasedRecommender;

    @Autowired
    private MahoutUserBasedCollaborativeRecommender userBasedCollaborativeRecommender;

    @ApiOperation(value = "", notes = "")
    @PostMapping(value = "/content")
    public AdaResponse<Boolean> recommendByContent() {


        List<Long> userList=new ArrayList<Long>();
        userList.add(1L);
        userList.add(2L);
        userList.add(3L);


        contentBasedRecommender.recommend(userList);

        return AdaResponse.success(true);

    }

    @ApiOperation(value = "", notes = "")
    @PostMapping(value = "/user")
    public AdaResponse<Boolean> recommendByUser() {


        List<Long> userList=new ArrayList<Long>();
        userList.add(1L);
        userList.add(2L);
        userList.add(3L);


        userBasedCollaborativeRecommender.recommend(userList);

        return AdaResponse.success(true);

    }


}
