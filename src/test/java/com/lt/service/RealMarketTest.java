package com.lt.service;

import com.lt.entity.RealMarket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;

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
    @Autowired
    BatchService batchService;

    @Test
    public void insert(){
        RealMarket realMarket = RealMarket.builder()
                .stockName("英威腾")
                .stockCode("002334")
                .dealNum(1l)
                .dealRmb(22)
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

    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        int week = c.get(Calendar.DAY_OF_WEEK);
        System.out.println(week);
    }
}
