package com.ada.news.dao.base;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * base mapper
 *
 * @author Laphi
 * @date 2019-05-23
 */
public interface AdaBaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
    //FIXME 特别注意，该接口不能被扫描到，否则会出错
}
