package com.lt.service;

import com.lt.entity.RealMarket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RealMarketServiceTest {
    @Autowired
    RealMarketService realMarketService;

    @Test
    public void getMarketList(){
        RealMarket realMarket = new RealMarket();
        realMarketService.getMarketList(realMarket);
    }
}