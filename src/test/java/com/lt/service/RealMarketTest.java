package com.lt.service;

import com.lt.entity.RealMarket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author gaijf
 * @description
 * @date 2019/9/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RealMarketTest {

    @Autowired
    RealMarketService realMarketService;

    @Test
    public void insert(){
        RealMarket realMarket = RealMarket.builder()
                .stockName("英威腾")
                .stockCode("002334")
                .nowPrice("5.06")
                .openPrice("dfsa")
                .closePrice("5.01")
                .dealNum("fdsf")
                .dealRmb("fdsadf")
                .dealTime("20190920153005")
                .rose("1.00")
                .exchange("11.1")
                .avgPrice(5.07)
                .timeMinute(201909201530L)
                .build();
        for (int i = 0;i < 10000;i++){
            realMarketService.insertRealMarket(realMarket);
        }
    }
}
