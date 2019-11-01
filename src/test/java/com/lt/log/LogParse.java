package com.lt.log;

import com.alibaba.fastjson.JSON;
import com.lt.common.BigDecimalUtil;
import com.lt.entity.RealMarket;
import com.lt.service.RealMarketService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaijf
 * @description
 * @date 2019/10/28
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogParse {

    @Autowired
    RealMarketService realMarketService;
    public int volamount = 0;
    public long minuteOld = 0l;

    public static void main(String[] args) throws IOException {
        List<String> fs = new ArrayList<>();
        fs.add("lt-stock.log.2019-10-30.0");
        fs.add("lt-stock.log.2019-10-30.1");
        fs.add("lt-stock.log.2019-10-30.2");
        fs.add("lt-stock.log.2019-10-30.3");
        LogParse logParse = new LogParse();
        List<RealMarket> realMarkets = logParse.readFile(fs);
        Map<String, List<RealMarket>> mapMinute = new HashMap<>();
        Map<String, List<RealMarket>> groupBy = realMarkets.stream().collect(Collectors.groupingBy(RealMarket::getStockCode));
        System.out.println("======================"+groupBy.size());
        for (Map.Entry<String, List<RealMarket>> entry : groupBy.entrySet()) {
            List<RealMarket> listMinute = new ArrayList<>();
            List<RealMarket> var = entry.getValue();
            double rmb = 0;
            for(int i = 0;i < var.size() - 1;i++){
                RealMarket rm = var.get(i);
                if(var.get(i+1).getTimeMinute() - rm.getTimeMinute() > 0){
                    listMinute.add(rm);
                    if (rm.getStockCode().equals("601857")){//603919 603101 002697
                        System.out.println(JSON.toJSONString(rm));;
                    }
                }else {
//                    if (rm.getStockCode().equals("601857")){//603919
//                        System.out.println(JSON.toJSONString(rm));;
//                    }
                    if (i == 0){
                        rmb = rm.getDealRmb();
                    }else {
                        if(Double.valueOf(rm.getRose()) - Double.valueOf(var.get(i-1).getRose()) >= 0){
                            rmb = BigDecimalUtil.add(rmb,rm.getDealRmb());
                        }else {
                            rmb = BigDecimalUtil.sub(rmb,rm.getDealRmb());
                        }
                    }
                }
            }
            mapMinute.put(entry.getKey(),listMinute);
        }
        List<ExchangeMom> result = new ArrayList<>();
        List<String> listDuration = logParse.filterDuration(mapMinute);
        List<ExchangeMom> listExchange = logParse.calculateExchange(mapMinute);
        for(ExchangeMom ex : listExchange){
            for (String code : listDuration){
                if (code.equals(ex.code) && ex.getMoneyFlow() > 0){
                    result.add(ex);
                    break;
                }
            }
        }

        System.out.println(result.size()+"======"+JSON.toJSONString(result));
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
        fs.add("lt-stock.log.2019-10-30.0");
        fs.add("lt-stock.log.2019-10-30.1");
        fs.add("lt-stock.log.2019-10-30.2");
        fs.add("lt-stock.log.2019-10-30.3");
        List<RealMarket> realMarkets = readFile(fs);
        Map<String, List<RealMarket>> groupBy = realMarkets.stream().collect(Collectors.groupingBy(RealMarket::getStockCode));
        for (Map.Entry<String, List<RealMarket>> entry : groupBy.entrySet()) {
            List<RealMarket> var = entry.getValue();
            for(int i = 0;i < var.size() - 1;i++){
                if(var.get(i+1).getTimeMinute() - var.get(i).getTimeMinute() > 0){
                    if (var.get(i).getStockCode().equals("603919")){
                        realMarketService.insertRealMarket(var.get(i));
                    }
                    if (var.get(i).getStockCode().equals("603101")){
                        realMarketService.insertRealMarket(var.get(i));
                    }
                    if (var.get(i).getStockCode().equals("002697")){
                        realMarketService.insertRealMarket(var.get(i));
                    }
                }
            }
        }

    }

    /**
     * 过滤出持续时间大于25分钟的数据
     * @param mapMinute
     * @return
     */
    public List<String> filterDuration(Map<String, List<RealMarket>> mapMinute){
        List<String> listDuration = new ArrayList<>();
        for (Map.Entry<String, List<RealMarket>> entry : mapMinute.entrySet()) {
            List<RealMarket> var = entry.getValue();
            for(RealMarket realMarket:var){
                if (realMarket.getDuration() >= 25){
                    listDuration.add(entry.getKey());
                    break;
                }
            }
        }
        System.out.println("LIST大listDuration小:"+listDuration.size()+",codes:"+JSON.toJSONString(listDuration));
        return listDuration;
    }
    /**
     * 换手率倍数计算并排序,并且换手率上涨20倍以上
     * @param mapMinute
     */
    public List<ExchangeMom> calculateExchange(Map<String, List<RealMarket>> mapMinute){
        List<ExchangeMom> listExchange = new ArrayList<>();
        for (Map.Entry<String, List<RealMarket>> entry : mapMinute.entrySet()) {
            List<RealMarket> var = entry.getValue();
            RealMarket first = var.get(1);
            RealMarket last = var.get(var.size()-1);
            double rose = Double.valueOf(last.getRose());
            if (rose > 3 || rose < -3){
                continue;
            }
            if (Double.valueOf(first.getExchange()) <= 0){
                first.setExchange("0.01");
            }
            double mom = BigDecimalUtil.div(Double.valueOf(last.getExchange()),Double.valueOf(first.getExchange()),2);
            if(mom < 20){
                continue;
            }
            ExchangeMom exchangeMom = new ExchangeMom();
            exchangeMom.setCode(entry.getKey());
            exchangeMom.setRose(mom);
            listExchange.add(exchangeMom);
        }
//        listExchange = listExchange.stream().sorted(Comparator.comparing(ExchangeMom::getRose).reversed()).collect(Collectors.toList());
        listExchange = listExchange.stream().sorted(Comparator.comparing(ExchangeMom::getMoneyFlow).reversed()).collect(Collectors.toList());
        System.out.println("LIST大listExchange小:"+listExchange.size()+",codes:"+JSON.toJSONString(listExchange));
        return listExchange;
    }

    class ExchangeMom {
        private String code;
        private double rose;
        private double moneyFlow;
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public double getRose() {
            return rose;
        }

        public void setRose(double rose) {
            this.rose = rose;
        }

        public double getMoneyFlow() {
            return moneyFlow;
        }

        public void setMoneyFlow(double moneyFlow) {
            this.moneyFlow = moneyFlow;
        }
    }


    public static List<RealMarket> readFile(List<String> fs) throws IOException {
        List<RealMarket> realMarkets = new ArrayList<>();
        for (String f : fs){
            FileInputStream fis=new FileInputStream("E:/logs/"+f);
            InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line="";
            String[] arrs=null;
            while ((line=br.readLine())!=null) {
                arrs=line.split("##");
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
