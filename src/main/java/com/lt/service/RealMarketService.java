package com.lt.service;

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

    public List<RealMarket> getMarketList(RealMarket realMarket) {
        return realMarketMapper.getMarketList(realMarket);
    }
}
