package com.ada.news.dao.mapper;

import com.ada.news.dao.base.AdaBaseMapper;
import com.ada.news.dao.entity.News;
import com.ada.news.dao.entity.NewsExample;

public interface NewsMapper extends AdaBaseMapper<News> {
    long countByExample(NewsExample example);
}