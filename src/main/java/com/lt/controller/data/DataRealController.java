package com.lt.controller.data;

import com.lt.common.exception.ResultEntity;
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
    public ResultEntity<List<RealMarket>> queryBriefMarketList(RealMarket realMarket,
                                              @RequestParam("pageNum") Integer pageNum,
                                              @RequestParam("pageSize") Integer pageSize){
        int count = realMarketService.queryBriefMarketListCount(realMarket);
        List<RealMarket> result = realMarketService.queryBriefMarketList(realMarket,PageParams.build(pageSize, pageNum));
        return ResultEntity.success(result,count);
    };

    @RequestMapping(value = "/clinch/detail",method = RequestMethod.GET)
    public String clinchDetail(){
        return "pages/data/real/clinchDetailList.html";
    }

    @RequestMapping(value = "/clinchDetailList",method = RequestMethod.GET)
    @ResponseBody
    public ResultEntity<List<RealMarket>> getClinchList(RealMarket realMarket,
                                                        @RequestParam("pageNum") Integer pageNum,
                                                        @RequestParam("pageSize") Integer pageSize){
        int count = realMarketService.getMarketListCount(realMarket);
        List<RealMarket> result = realMarketService.getMarketList(realMarket,PageParams.build(pageSize, pageNum));
        return ResultEntity.success(result,count);
    };
}
