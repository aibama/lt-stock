package com.lt.controller.data;

import com.lt.common.page.PageData;
import com.lt.common.page.PageParams;
import com.lt.entity.RealMarket;
import com.lt.service.RealMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public String clinch(){
        return "pages/data/real/clinchList.html";
    }

    @RequestMapping(value = "/clinchBriefList",method = RequestMethod.GET)
    @ResponseBody
    public PageData<RealMarket> queryBriefMarketList(RealMarket realMarket,
                                              @RequestParam("pageNum") Integer pageNum,
                                              @RequestParam("pageSize") Integer pageSize){
        PageData<RealMarket> result = realMarketService.queryBriefMarketList(realMarket,PageParams.build(pageSize, pageNum));
        return result;
    };

    @RequestMapping(value = "/clinch/detail",method = RequestMethod.GET)
    public String clinchDetail(){
        return "pages/data/real/clinchDetailList.html";
    }

    @RequestMapping(value = "/clinchDetailList",method = RequestMethod.GET)
    @ResponseBody
    public PageData<RealMarket> getClinchList( RealMarket realMarket,
                                              @RequestParam("pageNum") Integer pageNum,
                                              @RequestParam("pageSize") Integer pageSize){
//        RealMarket realMarket = RealMarket.builder()
//                .stockCode(stockCode)
//                .dealDate(dealDate)
//                .build();
        PageData<RealMarket> result = realMarketService.getMarketList(realMarket,PageParams.build(pageSize, pageNum));
        return result;
    };
}
