package com.lt.service;

import com.lt.common.page.PageData;
import com.lt.common.page.PageParams;
import com.lt.entity.RealMarket;
import com.lt.mapper.RealMarketMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/9/20
 */
@Slf4j
@Service
public class RealMarketService {

    @Autowired
    RealMarketMapper realMarketMapper;

    @Transactional(rollbackFor = Exception.class)
    public void insertRealMarket(RealMarket realMarket){
        realMarketMapper.insertRealMarket(realMarket);
    };

    public PageData<RealMarket> getMarketList(RealMarket realMarket, PageParams pageParams) {
        int totals = realMarketMapper.getMarketCount(realMarket);
        List<RealMarket> list = realMarketMapper.getMarketList(realMarket,pageParams);
        PageData<RealMarket> pageData = PageData.build(200,totals,list);
        return pageData;
    }
}
