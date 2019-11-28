package com.lt.config;

import com.lt.task.StockCodeFilter;
import com.lt.task.download.ClinchDownLoak;
import com.lt.task.download.PriceDownLoad;
import com.lt.task.extract.RealMarketExtract;
import com.lt.task.transform.RealMarketFilter;
import com.lt.task.transform.RealMarketTransform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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

    @Bean
    public ClinchDownLoak clinchDownLoak(){
        ClinchDownLoak clinchDownLoak = new ClinchDownLoak();
        return clinchDownLoak;
    }

    @Bean
    public PriceDownLoad priceDownLoad(){
        PriceDownLoad priceDownLoad = new PriceDownLoad();
        return priceDownLoad;
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
//    @Bean
//    public RealMarketFilter realMarketFilter(){
//        RealMarketFilter realMarketFilter = new RealMarketFilter();
//        return realMarketFilter;
//    }

    @Bean
    @ConditionalOnBean(RealMarketExtract.class)
    public RealMarketTransform realMarketTransform(){
        RealMarketTransform realMarketTransform = new RealMarketTransform();
        return realMarketTransform;
    }
}
