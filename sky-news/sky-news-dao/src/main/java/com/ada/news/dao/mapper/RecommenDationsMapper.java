package com.ada.news.dao.mapper;

import com.ada.news.dao.base.AdaBaseMapper;
import com.ada.news.dao.entity.RecommenDations;
import com.ada.news.dao.entity.RecommenDationsExample;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface RecommenDationsMapper extends AdaBaseMapper<RecommenDations> {
    long countByExample(RecommenDationsExample example);

    RecommenDations findFirst(@Param("certainTimestamp") Date certainTimestamp, @Param("userId") Long userId);
}