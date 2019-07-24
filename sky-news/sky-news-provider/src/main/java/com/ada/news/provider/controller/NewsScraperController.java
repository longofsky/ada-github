package com.ada.news.provider.controller;

import com.ada.common.concurrent.MultithreadExecutor;
import com.ada.common.util.AdaResponse;
import com.ada.news.service.help.NewsScraperService;
import com.ada.news.service.process.NewsScraperProcess;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @ProjectName: ada-github
 * @Package: com.ada.news.provider.controller
 * @ClassName: NewsScraperController
 * @Author: litianlong
 * @Description: ${description}
 * @Date: 2019-07-22 15:25
 * @Version: 1.0
 */
@RestController
@RequestMapping("/news")
public class NewsScraperController {

    Logger logger = LoggerFactory.getLogger(NewsScraperController.class);

    @Autowired
    private NewsScraperService newsScraperService;

    @ApiOperation(value = "", notes = "")
    @PostMapping(value = "/scraper")
    public AdaResponse<Boolean> recommendByArticle() {

        MultithreadExecutor.getInstance().doAsyn(new NewsScraperProcess(newsScraperService));

        return AdaResponse.success(true);
    }

}
