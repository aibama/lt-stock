package com.lt.service;

import com.alibaba.fastjson.JSON;
import com.lt.common.page.PageData;
import com.lt.common.page.PageParams;
import com.lt.entity.RealMarket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RealMarketServiceTest {
    @Autowired
    RealMarketService realMarketService;

    @Test
    public void getMarketList(){
        RealMarket realMarket = new RealMarket();
        List<RealMarket> pageData = realMarketService.getMarketList(realMarket,PageParams.build(10, 2));
        System.out.println(JSON.toJSONString(pageData));
    }
}