package com.ada.news.dao.mapper;

import com.ada.news.dao.base.AdaBaseMapper;
import com.ada.news.dao.entity.NewsModules;
import com.ada.news.dao.entity.NewsModulesExample;

public interface NewsModulesMapper extends AdaBaseMapper<NewsModules> {
    long countByExample(NewsModulesExample example);
}