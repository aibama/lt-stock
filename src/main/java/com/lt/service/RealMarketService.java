package com.lt.service;

import com.lt.common.page.PageParams;
import com.lt.entity.RealMarket;
import com.lt.mapper.RealMarketMapper;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 获取实时成交数据详细信息总条数
     * @param realMarket
     * @return
     */
    public int getMarketListCount(RealMarket realMarket) {
        int totals = realMarketMapper.getMarketCount(realMarket);
        return totals;
    }

    /**
     * 获取实时成交数据详细信息
     * @param realMarket
     * @param pageParams
     * @return
     */
    public List<RealMarket> getMarketList(RealMarket realMarket, PageParams pageParams) {
        List<RealMarket> list = realMarketMapper.getMarketList(realMarket,pageParams);
        return list;
    }

    /**
     * 获取实时成交数据简要信息总条数
     * @param realMarket
     * @return
     */
    public int queryBriefMarketListCount(RealMarket realMarket) {
        int totals = realMarketMapper.queryBriefMarketCount(realMarket);
        return totals;
    }

    /**
     * 获取实时成交数据简要信息
     * @param realMarket
     * @param pageParams
     * @return
     */
    public List<RealMarket> queryBriefMarketList(RealMarket realMarket, PageParams pageParams) {
        List<RealMarket> list = realMarketMapper.queryBriefMarketList(realMarket,pageParams);
        return list;
    }
}
