package com.lt.service;

import com.lt.entity.RealMarket;
import com.lt.mapper.RealMarketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gaijf
 * @description
 * @date 2019/9/20
 */
@Service
public class RealMarketService {

    @Autowired
    RealMarketMapper realMarketMapper;

    @Transactional(rollbackFor = Exception.class)
    public void insertRealMarket(RealMarket realMarket){
        realMarketMapper.insertRealMarket(realMarket);
    };
}
