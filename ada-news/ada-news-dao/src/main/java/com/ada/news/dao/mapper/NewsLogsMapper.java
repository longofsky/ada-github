package com.ada.news.dao.mapper;

import com.ada.news.dao.base.AdaBaseMapper;
import com.ada.news.dao.entity.NewsLogs;
import com.ada.news.dao.entity.NewsLogsExample;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface NewsLogsMapper extends AdaBaseMapper<NewsLogs> {
    long countByExample(NewsLogsExample example);

    List<NewsLogs> find(@Param("day") Date day);
}