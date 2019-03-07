package com.wootion.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wootion.commons.result.PageInfo;
import com.wootion.commons.utils.CRC16Util;
import com.wootion.commons.utils.MyByte;
import com.wootion.commons.utils.RXTXUtils;
import com.wootion.commons.utils.SerialTool;
import com.wootion.mapper.AiringMapper;
import com.wootion.model.Airing;
import com.wootion.service.IAiringService;
import gnu.io.SerialPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Airing 表数据服务层接口实现类
 */
@Service
public class AiringServiceImpl extends ServiceImpl<AiringMapper, Airing> implements IAiringService{

    protected Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private AiringMapper airingMapper;

    @Override
    public void selectDataGrid(PageInfo pageInfo) {
        Page<Map<String, Object>> page = new Page<Map<String, Object>>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = airingMapper.selectAiringPage(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    @Override
    public int selectCountByCondition(String deviceName) {
        EntityWrapper<Airing> wrapper = new EntityWrapper<Airing>();
        wrapper.orderBy("id");
        wrapper.where("device_name = '"+ deviceName+"'");
        return airingMapper.selectCount(wrapper);
    }


    public  void openAiring(String com,int deviceNum) throws Exception{
        SerialTool tool = SerialTool.getSerialTool();
        //打开com
        SerialPort serialPort= tool.openPort(com, 115200);
        //组装数据
        String str = Integer.toHexString(deviceNum);
        if(str.length()==1){
            str="0"+str;
        }
        String openStr = str+"0600000001";
        byte[] bstr = new BigInteger(openStr, 16).toByteArray();
        openStr=openStr+String.format("%04x", CRC16Util.calcCrc16(bstr)).toUpperCase();
        logger.info("打开功能，发送字符串："+openStr);
        //发送数据
        tool.sendToPort(serialPort, MyByte.HexString2Bytes(openStr));
        //关闭的串口对象
        tool.closePort(serialPort);
    }


    public  void closeAiring(String com,int deviceNum)throws Exception{
        SerialTool tool = SerialTool.getSerialTool();
        //打开com
        SerialPort serialPort=tool.openPort(com, 115200);
        //组装数据
        String str = Integer.toHexString(deviceNum);
        if(str.length()==1){
            str="0"+str;
        }
        System.out.println(str);
        String closeStr = str+"0600000000";
        byte[] bstr = new BigInteger(closeStr, 16).toByteArray();
        closeStr=closeStr+String.format("%04x", CRC16Util.calcCrc16(bstr)).toUpperCase();
        logger.info("关闭功能，发送字符串："+closeStr);
        //发送数据
        tool.sendToPort(serialPort, MyByte.HexString2Bytes(closeStr));
        //关闭的串口对象
        tool.closePort(serialPort);
    }

}
