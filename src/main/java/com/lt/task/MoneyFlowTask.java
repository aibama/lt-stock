package com.lt.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author gaijf
 * @description
 * @date 2019/9/17
 */
@Slf4j
public class MoneyFlowTask {

    @Scheduled(cron = "0/30 * * * * *")
    public void execute(){
//        log.info("资金流向执行");
    }
}
