package com.lt.redis;

import com.lt.common.RedisUtil;
import com.lt.utils.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
}

