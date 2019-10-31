package com.lt.config;

import com.lt.task.StockCodeFilter;
import com.lt.task.extract.RealMarketExtract;
import com.lt.task.transform.RealMarketFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * @author gaijf
 * @description
 * @date 2019/9/17
 */
@Configuration
@EnableScheduling
public class TaskConfig implements SchedulingConfigurer {

    /**
     * 设置线程池类型，默认是单线程池执行
     * @param taskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(4));
    }

//    @Bean
//    public StockCodeFilter stockCodeFilter(){
//        StockCodeFilter stockCodeFilter = new StockCodeFilter();
//        return stockCodeFilter;
//    }
//
//    @Bean
//    public RealMarketExtract realMarketExtract(){
//        RealMarketExtract realMarketExtract = new RealMarketExtract();
//        return realMarketExtract;
//    }
//
    @Bean
    public RealMarketFilter realMarketFilter(){
        RealMarketFilter realMarketFilter = new RealMarketFilter();
        return realMarketFilter;
    }
}
