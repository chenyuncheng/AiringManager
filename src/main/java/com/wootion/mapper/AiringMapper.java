package com.wootion.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.wootion.model.Airing;

import java.util.List;
import java.util.Map;

/**
 * Airing表数据库控制层接口
 */
public interface AiringMapper extends BaseMapper<Airing> {

    List<Map<String, Object>> selectAiringPage(Pagination page, Map<String, Object> params);

    Integer selectCountByCondition(Map<String,Object> params);

}
