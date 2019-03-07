package com.wootion.service;

import com.baomidou.mybatisplus.service.IService;
import com.wootion.commons.result.PageInfo;
import com.wootion.model.SysLog;

/**
 *
 * SysLog 表数据服务层接口
 *
 */
public interface ISysLogService extends IService<SysLog> {

    void selectDataGrid(PageInfo pageInfo);

}