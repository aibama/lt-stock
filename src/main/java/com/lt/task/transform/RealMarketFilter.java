package com.lt.task.transform;

import com.alibaba.fastjson.JSON;
import com.lt.common.MailUtil;
import com.lt.common.RedisUtil;
import com.lt.common.TimeUtil;
import com.lt.entity.RealMarket;
import com.lt.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author gaijf
 * @description
 * @date 2019/9/22
 */
@Slf4j
public class RealMarketFilter {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    MailUtil mailUtil;

    @Scheduled(cron = "0 0/15 * * * ?")//每15分钟执行一次
    public void execute() {
        try{
            if (TimeUtil.isEffectiveDate("09:30:00","11:30:00","HH:mm:ss")
                    || TimeUtil.isEffectiveDate("12:59:59","15:00:00","HH:mm:ss")){
                Set<String> set = redisUtil.revRange(Constants.REAL_MARKET_TF,0,-1);
                List<RealMarket> results = new ArrayList(set.size());
                for (String str : set){
                    results.add(JSON.parseObject(redisUtil.get(str),RealMarket.class));
                }
                List<String> listDuration = this.sendDuration(results);
                List<String> listExchange = this.sendExchange(results);
                this.sendSynthesis(listDuration,listExchange);
            }
        }catch (Exception e){
            log.info("数据过滤异常:{}",e);
        }
    }

    /**
     * 综合排序
     * @param listDuration
     * @param listExchange
     */
    public void sendSynthesis(List<String> listDuration,List<String> listExchange){
        List<Synthesis> var = new ArrayList<>();
        for (int i = 0;i < listDuration.size();i++){
            String code = listDuration.get(i);
            for (int y = 0;y < listExchange.size();y++){
                if (code.equals(listExchange.get(y))){
                    Synthesis synthesisSort = new Synthesis();
                    synthesisSort.setCode(code);
                    synthesisSort.setScore(i+y);
                    var.add(synthesisSort);
                }
            }
        }
        List<String> listSynthesisSort = var.stream()
                .sorted(Comparator.comparing(Synthesis::getScore).reversed())
                .map(Synthesis::getCode)
                .collect(Collectors.toList());
        log.info("综合排序{}",JSON.toJSONString(listSynthesisSort));
//        mailUtil.sendSimpleMail("gjf0519@163.com","综合排序",JSON.toJSONString(listSynthesisSort));
    }

    /**
     * 换手率排序
     * @param results
     */
    public List<String> sendExchange(List<RealMarket> results){
        List<String> listExchange = results.stream()
                .sorted(Comparator.comparing(RealMarket::getExchange).reversed())
                .map(RealMarket::getStockCode)
                .collect(Collectors.toList());
        log.info("换手率排序{}",JSON.toJSONString(listExchange));
//        mailUtil.sendSimpleMail("gjf0519@163.com","换手率排序",JSON.toJSONString(listExchange));
        return listExchange;
    }

    /**
     * 持久时间排行
     * @param results
     */
    public List<String> sendDuration(List<RealMarket> results){
        List<String> listDuration = results.stream()
                .map(RealMarket::getStockCode)
                .collect(Collectors.toList());
        log.info("持久排序{}",JSON.toJSONString(listDuration));
//        mailUtil.sendSimpleMail("gjf0519@163.com","持久排序",JSON.toJSONString(listDuration));
        return listDuration;
    }

    /**
     * 根据振幅过滤
     * @param rose
     * @return
     */
    public boolean roseFilter(double rose){
        if (rose > 3 || rose < -3){
            return true;
        }
        return false;
    }

    /**
     * 根据均值持续时间过滤
     * @param duration
     * @return
     */
    public boolean durationFilter(int duration){
        if (duration < 20)
            return true;
        return false;
    }

    private class Synthesis implements Serializable {
        private String code;
        private int score;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

    public static void main(String[] args) {
        Stream.of(1,4,8,5,7,9).sorted().forEach(System.out::println);
    }
}
