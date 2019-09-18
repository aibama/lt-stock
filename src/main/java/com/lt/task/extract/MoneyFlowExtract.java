package com.lt.task.extract;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author gaijf
 * @description
 * @date 2019/9/18
 */
@Slf4j
public class MoneyFlowExtract {

    @Scheduled(cron = "0/30 * * * * *")
    public void execute(){
//        log.info("资金流向执行");
    }
}
