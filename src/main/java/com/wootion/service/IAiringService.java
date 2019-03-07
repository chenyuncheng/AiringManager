package com.wootion.service;

import com.baomidou.mybatisplus.service.IService;
import com.wootion.commons.result.PageInfo;
import com.wootion.model.Airing;

import java.util.Map;

/**
 * Airing 表数据服务层接口
 */
public interface IAiringService extends IService<Airing> {

    void selectDataGrid(PageInfo pageInfo);

    int selectCountByCondition(String deviceName);

    /**
     * 打开喇叭
     * @param com 串口名
     * @param deviceNum 设备编号
     */
    void openAiring(String com,int deviceNum)throws Exception;

    /**
     * 关闭喇叭
     * @param com 串口名
     * @param deviceNum 设备编号
     */
    void closeAiring(String com,int deviceNum)throws Exception;
}
