package com.lt.redis;

import com.alibaba.fastjson.JSON;
import com.lt.common.RedisUtil;
import com.lt.entity.RealMarket;
import com.lt.utils.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gaijf
 * @description
 * @date 2019/9/18
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisUtil redisUtil;

    @Test
    public void RedisProducer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0;i<10;i++) {
                    redisUtil.rPush("listingList","value_" + i);
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for(int i = 0;i<10;i++) {
                    redisUtil.rPush("listingList","value2_" + i);
                }
            }
        }).start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true){
            //阻塞式brpop，List中无数据时阻塞，参数0表示一直阻塞下去，直到List出现数据
            String listingList = redisUtil.lPop("listingList",0);
            System.out.println(listingList);
        }
    }

    @Test
    public void pushMsg(){
        for(int i = 0;i < 10;i++){
            redisUtil.convertAndSend(Constants.PRESET_REAL_PRICE,"223333");
        }
    }

    @Test
    public void zset(){
        Set<String> set = redisUtil.revRange(Constants.REAL_MARKET_TF,0,-1);
        List<RealMarket> results = new ArrayList(set.size());
        for (String str : set){
            RealMarket realMarket = JSON.parseObject(redisUtil.get(str),RealMarket.class);
            if (realMarket == null)
                System.out.println(str);
            results.add(realMarket);
        }

        List<String> listExchange = results.stream()
                .sorted(Comparator.comparing(RealMarket::getExchange).reversed())
                .map(o -> o.getStockCode())
                .collect(Collectors.toList());
        System.out.println("换手率排序"+JSON.toJSONString(listExchange));
    }
}

