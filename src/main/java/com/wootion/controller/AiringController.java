package com.wootion.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.wootion.commons.base.BaseController;
import com.wootion.commons.result.PageInfo;
import com.wootion.commons.utils.SerialTool;
import com.wootion.commons.utils.StringUtils;
import com.wootion.model.Airing;
import com.wootion.service.IAiringService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 广播设备管理
 */
@Controller
@RequestMapping("/airing")
public class AiringController extends BaseController {

    protected Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private IAiringService airingService;

    public String test(){

        return "test";
    }

    /**
     * 设备管理页
     *
     * @return
     */
    @GetMapping("/manager")
    public String manager() {
        return "admin/airing/airing";
    }

    /**
     * 设备管理查询
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @param airing
     * @return
     */
    @PostMapping("/dataGrid")
    @ResponseBody
    public Object dataGrid(Integer page, Integer rows, String sort, String order,Airing airing) {
        PageInfo pageInfo = new PageInfo(page, rows, sort, order);
        Map<String, Object> condition = new HashMap<>();

        if (airing.getComNum()!=null&&airing.getComNum()!="") {
            condition.put("comNum", airing.getComNum());
        }
        //Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if(airing.getDeviceNum()!=null){
            condition.put("deviceNum", airing.getDeviceNum());
        }
        if(airing.getStatus()!=null){
            if(airing.getStatus()==0||airing.getStatus()==1)
                condition.put("status", airing.getStatus());
        }

        pageInfo.setCondition(condition);
        airingService.selectDataGrid(pageInfo);
        return pageInfo;
    }

    /**
     * 添加设备页
     *
     * @return
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "admin/airing/airingAdd";
    }

    /**
     * 添加设备
     *
     * @param airing
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Object add(@Valid Airing airing) {
        int num = airingService.selectCountByCondition(airing.getDeviceName());
        if (num>0) {
            return renderError("该设备已存在!");
        }
        airing.setCreateTime(new Date());
        airing.setUserName(getStaffName());
        airingService.insert(airing);
        logger.info("设备（"+airing.getDeviceName()+")添加成功!");
        return renderSuccess("设备（"+airing.getDeviceName()+")添加成功!");
    }

    /**
     * 编辑设备页
     *
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/editPage")
    public String editPage(Model model, Long id) {
        Airing airing = airingService.selectById(id);
        model.addAttribute("airing", airing);
        return "admin/airing/airingEdit";
    }

    /**
     * 编辑设备
     *
     * @param airing
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(@Valid Airing airing) {
        Airing oldAiring = airingService.selectById(airing.getId());
        airingService.updateById(airing);
        logger.info("设备（"+oldAiring.getDeviceName()+")修改成功！");
        return renderSuccess("编辑成功！");
    }

    /**
     * 删除设备
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(Long id) {
        airingService.deleteById(id);
        logger.info("设备（id："+id+"）删除成功");
        return renderSuccess("删除成功！");
    }

    /**
     * 打开或关闭设备页
     *
     * @param model
     * @return
     */
    @GetMapping("/openOrClosePage")
    public String openOrClosePage(Model model) {
        //Airing airing = airingService.selectById(id);
        List<Airing> allList =  airingService.selectByMap(null);
        model.addAttribute("list", allList);
        return "admin/airing/openOrClosePage";
    }


    /**
     * 打开设备
     * @param id
     * @return
     */
    @RequestMapping("/open")
    @ResponseBody
    public Object openAiring(Long id){
        Airing airing = airingService.selectById(id);
        try{
            airingService.openAiring(airing.getComNum(),airing.getDeviceNum());
            airing.setStatus(1);
            airingService.updateById(airing);
        }catch (Exception e){
            logger.error("打开设备失败！失败原因："+e.getMessage());
            return renderError("打开设备失败！失败原因："+e.getMessage());
        }
        logger.info("成功打开("+airing.getDeviceName()+")设备！");
        return renderSuccess("成功打开("+airing.getDeviceName()+")设备！");
    }

    /**
     * 关闭设备
     * @param id
     * @return
     */
    @RequestMapping("/close")
    @ResponseBody
    public Object closeAiring(Long id){
        Airing airing = airingService.selectById(id);
        try{
            airingService.closeAiring(airing.getComNum(),airing.getDeviceNum());
            airing.setStatus(0);
            airingService.updateById(airing);
        }catch (Exception e){
            logger.error("关闭设备失败！失败原因："+e.getMessage());
            return renderError("打开设备失败！失败原因："+e.getMessage());
        }
        logger.info("成功关闭("+airing.getDeviceName()+")设备！");
        return renderSuccess("成功关闭("+airing.getDeviceName()+")设备！");
    }

}
