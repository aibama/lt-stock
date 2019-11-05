package com.lt.task.download;

import com.alibaba.fastjson.JSON;
import com.lt.common.FileUtil;
import com.lt.common.TimeUtil;
import com.lt.entity.CapitalInfo;
import com.lt.entity.ClinchDetail;
import com.lt.entity.ClinchInfo;
import com.lt.entity.PriceInfo;
import com.lt.service.CapitalService;
import com.lt.utils.Constants;
import com.lt.utils.RealCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gaijf
 * @description 股票基本信息数据下载
 * @date 2019/11/3
 */
@Slf4j
public class PriceDownLoad {

    @Autowired
    CapitalService capitalService;
    @Autowired
    private RestTemplate restTemplate;
    private CountDownLatch latch = null;
    private static final ThreadPoolExecutor excutor = new ThreadPoolExecutor(2,8,20, TimeUnit.SECONDS,new LinkedBlockingDeque<>(3000));

    @Scheduled(cron = "0 30 19 * * ?")
    public void execute() {
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<List<String>> listCodes = RealCodeUtil.getCodesList(1000, Arrays.asList(codeArray));
        latch = new CountDownLatch(listCodes.size());
        for (int i = 0; i < listCodes.size(); i++) {
            excutor.execute(new DownLoadThread(listCodes.get(i),restTemplate,latch,0));
        }
        /**
         * 遇到的问题：由于主线程结束，导致创建的线程未正常执行完成就被动结束
         */
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String directory = "E:\\excel\\stock\\price\\";
        //解析股票基本信息数据
        this.parseCsv(directory);
    }

    private void parseCsv(String directory){
        List<String> fileNames = FileUtil.getAllFileName(directory);
        for (String fileName:fileNames){
            String storagePath = directory+fileName;
            List<PriceInfo> csvData = FileUtil.getCsvData(storagePath, PriceInfo.class);
            String code = fileName.substring(0,7);
            for(PriceInfo info:csvData){
                double rose = info.getRose().equals("None") ? 0:Double.valueOf(info.getRose());
                CapitalInfo capitalInfo = CapitalInfo.builder()
                        .stockCode(code)
                        .stockName(info.getStockName())
                        .capitalSize(info.getVaturnover())
                        .exchange(info.getExchange())
                        .rose(rose)
                        .voturnover(info.getVoturnover())
                        .dealTime(info.getDealTime())
                        .circulationCap(info.getMcap())
                        .mktCap(info.getTcap())
                        .build();
                capitalService.insertCapital(capitalInfo);
            }
        }
    }
}
