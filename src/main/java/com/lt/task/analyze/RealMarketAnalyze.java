package com.lt.task.analyze;

import com.alibaba.fastjson.JSON;
import com.lt.entity.RealMarket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gaijf
 * @description 实时交易数据分析
 * @date 2019/9/21
 */
@Slf4j
@Component
public class RealMarketAnalyze {

    public void receiveMessage(String message) {
        RealMarket realMarket = JSON.parseObject(message,RealMarket.class);
    }

}
