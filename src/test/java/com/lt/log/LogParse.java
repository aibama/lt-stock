package com.lt.log;

import com.alibaba.fastjson.JSON;
import com.lt.entity.RealMarket;
import com.lt.service.RealMarketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gaijf
 * @description
 * @date 2019/10/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogParse {

    @Autowired
    RealMarketService realMarketService;
    public int volamount = 0;
    public long minuteOld = 0l;

    public static void main(String[] args) throws IOException {
        List<String> fs = new ArrayList<>();
        fs.add("lt-stock.log.2019-10-29.0");
        fs.add("lt-stock.log.2019-10-29.1");
        fs.add("lt-stock.log.2019-10-29.2");
        fs.add("lt-stock.log");
        LogParse logParse = new LogParse();
        List<RealMarket> realMarkets = logParse.readFile(fs);
        Map<String, List<RealMarket>> groupBy = realMarkets.stream().collect(Collectors.groupingBy(RealMarket::getStockCode));
        for (Map.Entry<String, List<RealMarket>> entry : groupBy.entrySet()) {
            List<RealMarket> var = entry.getValue();
            if(entry.getKey().equals("002269")){
                logParse.volamount=0;
                logParse.minuteOld=0l;
                for(int i = 0;i < var.size() - 1;i++){
                    logParse.isMinute(var.get(i));
                }
                for(int i = 0;i < var.size() - 1;i++){
                    if(var.get(i+1).getTimeMinute() - var.get(i).getTimeMinute() > 0){
                        System.out.println(JSON.toJSONString(var.get(i)));
                    }
                }
            }
            if(entry.getKey().equals("600371")){
                logParse.volamount=0;
                logParse.minuteOld=0l;
                for(int i = 0;i < var.size() - 1;i++){
                    logParse.isMinute(var.get(i));
                }
                for(int i = 0;i < var.size() - 1;i++){
                    if(var.get(i+1).getTimeMinute() - var.get(i).getTimeMinute() > 0){
                        System.out.println(JSON.toJSONString(var.get(i)));
                    }
                }
            }
            if(entry.getKey().equals("600898")){
                logParse.volamount=0;
                logParse.minuteOld=0l;
                for(int i = 0;i < var.size() - 1;i++){
                    logParse.isMinute(var.get(i));
                }
                for(int i = 0;i < var.size() - 1;i++){
                    if(var.get(i+1).getTimeMinute() - var.get(i).getTimeMinute() > 0){
                        System.out.println(JSON.toJSONString(var.get(i)));
                    }
                }
            }
        }
    }

    public void isMinute(RealMarket realMarket){
        //每分钟计算一次均价
        long minuteNew = Long.valueOf(realMarket.getDealTime().substring(0,12));
        if ((minuteNew - minuteOld) >= 1){
            minuteOld = minuteNew;
            realMarket.setVolamount(0);
            realMarket.setTimeMinute(minuteNew);
            volamount = 0;
        }else {
            volamount = volamount+1;
            realMarket.setVolamount(volamount);
            realMarket.setTimeMinute(minuteNew);
        }
    }

    @Test
    public void readFile() throws IOException {
        List<String> fs = new ArrayList<>();
        fs.add("lt-stock.log.2019-10-29.0");
        fs.add("lt-stock.log.2019-10-29.1");
        fs.add("lt-stock.log.2019-10-29.2");
        fs.add("lt-stock.log.2019-10-29.3");
        List<RealMarket> realMarkets = this.readFile(fs);
        Map<String, List<RealMarket>> groupBy = realMarkets.stream().collect(Collectors.groupingBy(RealMarket::getStockCode));
        for (Map.Entry<String, List<RealMarket>> entry : groupBy.entrySet()) {
//            List<RealMarket> var = entry.getValue();
//            for(int i = 0;i < var.size() - 1;i++){
//                if(var.get(i+1).getTimeMinute() - var.get(i).getTimeMinute() > 0){
//                    if (var.get(i).getStockCode().equals("603366")){
//                        realMarketService.insertRealMarket(var.get(i));
//                    }
//                }
//            }
            List<RealMarket> var = entry.getValue();
            if(entry.getKey().equals("002269")){
                this.volamount=0;
                this.minuteOld=0l;
                for(int i = 0;i < var.size() - 1;i++){
                    this.isMinute(var.get(i));
                }
                for(int i = 0;i < var.size() - 1;i++){
                    if(var.get(i+1).getTimeMinute() - var.get(i).getTimeMinute() > 0){
                        realMarketService.insertRealMarket(var.get(i));
                    }
                }
            }
            if(entry.getKey().equals("600371")){
                this.volamount=0;
                this.minuteOld=0l;
                for(int i = 0;i < var.size() - 1;i++){
                    this.isMinute(var.get(i));
                }
                for(int i = 0;i < var.size() - 1;i++){
                    if(var.get(i+1).getTimeMinute() - var.get(i).getTimeMinute() > 0){
                        realMarketService.insertRealMarket(var.get(i));
                    }
                }
            }
            if(entry.getKey().equals("600898")){
                this.volamount=0;
                this.minuteOld=0l;
                for(int i = 0;i < var.size() - 1;i++){
                    this.isMinute(var.get(i));
                }
                for(int i = 0;i < var.size() - 1;i++){
                    if(var.get(i+1).getTimeMinute() - var.get(i).getTimeMinute() > 0){
                        realMarketService.insertRealMarket(var.get(i));
                    }
                }
            }
        }
    }


    public List<RealMarket> readFile(List<String> fs) throws IOException {
        List<RealMarket> realMarkets = new ArrayList<>();
        for (String f : fs){
            FileInputStream fis=new FileInputStream("E:/logs/"+f);
            InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line="";
            String[] arrs=null;
            while ((line=br.readLine())!=null) {
                arrs=line.split("1#1:");
                if (arrs.length < 2)
                    continue;
                RealMarket realMarket = JSON.parseObject(arrs[1],RealMarket.class);
                realMarkets.add(realMarket);
//                if (realMarket.getStockCode().equals("002269")){
//                    System.out.println(JSON.toJSONString(realMarket));
//                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        return realMarkets;
    }
}
