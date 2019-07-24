package com.ada.news.service.process;

import com.ada.common.concurrent.IProcess;
import com.ada.common.utils.JsonUtil;
import com.ada.news.service.help.NewsScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;

/**
 * @Description 异步处理 新闻抓去任务
 * @Author litianlong
 * @Date 2019-07-24 10:36
 */
public class  NewsScraperProcess implements IProcess {

    Logger log = LoggerFactory.getLogger(NewsScraperProcess.class);

    private NewsScraperService newsScraperService;

    public NewsScraperProcess(NewsScraperService newsScraperService) {
        this.newsScraperService = newsScraperService;
    }

    /**
     * 此处完成 异步埋点数据发送到Kafka的逻辑
     */
    @Override
    public <T> T process() {

        try {
            newsScraperService.newsScraper();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
