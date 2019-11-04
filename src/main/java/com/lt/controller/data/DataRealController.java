package com.lt.controller.data;

import com.lt.entity.RealMarket;
import com.lt.service.RealMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author gaijf
 * @description 实时数据查询接口
 * @date 2019/11/4
 */
@Controller
@RequestMapping(value = "/real")
public class DataRealController {

    @Autowired
    RealMarketService realMarketService;

    @RequestMapping(value = "/clinch",method = RequestMethod.GET)
    public String clinchList(RealMarket realMarket,ModelMap map){
        List<RealMarket> result = realMarketService.getMarketList(realMarket);
        map.addAttribute("realClinchList",result);
        return "pages/data/real/clinchList.html";
    }
}
