package com.ada.news.dao.mapper;

import com.ada.news.dao.base.AdaBaseMapper;
import com.ada.news.dao.entity.Users;
import com.ada.news.dao.entity.UsersExample;
import org.apache.ibatis.annotations.Param;

public interface UsersMapper extends AdaBaseMapper<Users> {
    long countByExample(UsersExample example);

    void updatePrelistById(@Param("beanToJson") String beanToJson, @Param("userId")Long userId);
}