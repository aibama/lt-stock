package com.lt.service;

import com.alibaba.fastjson.JSON;
import com.lt.entity.RealMarket;
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

/**
 * @author gaijf
 * @description
 * @date 2019/9/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RealMarketTest {

    @Autowired
    RealMarketService realMarketService;
    @Autowired
    BatchService batchService;

    @Test
    public void insert(){
        RealMarket realMarket = RealMarket.builder()
                .stockName("英威腾")
                .stockCode("002334")
                .nowPrice("5.06")
                .openPrice("dfsa")
                .closePrice("5.01")
                .dealNum(1l)
                .dealRmb(22)
                .dealTime("20190920153005")
                .rose("1.00")
                .exchange("11.1")
                .avgPrice(5.07)
                .timeMinute(201909201530L)
                .build();
        for (int i = 0;i < 10000;i++){
            realMarketService.insertRealMarket(realMarket);
        }
    }

    @Test
    public void readFile() throws IOException {
        List<String> fs = new ArrayList<>();
        fs.add("lt-stock.log.2019-09-20.0");
        fs.add("lt-stock.log.2019-09-20.1");
        fs.add("lt-stock.log.2019-09-20.2");
        fs.add("lt-stock.log.2019-09-20.3");
        fs.add("lt-stock.log.2019-09-20.4");
        fs.add("lt-stock.log.2019-09-20.5");
        fs.add("lt-stock.log.2019-09-20.6");
        fs.add("lt-stock.log.2019-09-20.7");
        fs.add("lt-stock.log.2019-09-20.8");
        fs.add("lt-stock.log.2019-09-20.9");
        fs.add("lt-stock.log.2019-09-20.10");
        fs.add("lt-stock.log.2019-09-20.11");
        fs.add("lt-stock.log.2019-09-20.12");
        fs.add("lt-stock.log.2019-09-20.13");
        fs.add("lt-stock.log.2019-09-20.14");
        fs.add("lt-stock.log.2019-09-20.15");
        fs.add("lt-stock.log.2019-09-20.16");
        fs.add("lt-stock.log");
        for (String f : fs){
            long start = System.currentTimeMillis();
            FileInputStream fis=new FileInputStream("D:/logs/"+f);
            InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line="";
            String[] arrs=null;
            List<RealMarket> list = new ArrayList<>(1000);
            while ((line=br.readLine())!=null) {
                arrs=line.split("1#1");
                if (arrs.length < 2)
                    continue;
                RealMarket realMarket = JSON.parseObject(arrs[1],RealMarket.class);
                if (list.size() <= 1000) {
                    list.add(realMarket);
                }else {
                    batchService.batchRealMarket(list);
                    list.clear();
                }
            }
            if (list.size() > 0){
                batchService.batchRealMarket(list);
            }
            br.close();
            isr.close();
            fis.close();
            System.out.println(System.currentTimeMillis() - start+"=======================");
            return;
        }
    }
}
