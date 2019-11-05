package com.lt.task.download;

import com.alibaba.fastjson.JSON;
import com.lt.common.BigDecimalUtil;
import com.lt.common.FileUtil;
import com.lt.common.TimeUtil;
import com.lt.entity.ClinchDetail;
import com.lt.entity.ClinchInfo;
import com.lt.service.ClinchService;
import com.lt.utils.Constants;
import com.lt.utils.RealCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gaijf
 * @description 交易明细信息
 * @date 2019/11/3
 */
@Slf4j
public class ClinchDownLoak {
    @Autowired
    ClinchService clinchService;
    @Autowired
    private RestTemplate restTemplate;
    private CountDownLatch latch = null;
    private static final ThreadPoolExecutor excutor = new ThreadPoolExecutor(2,8,20, TimeUnit.SECONDS,new LinkedBlockingDeque<>(3000));

    @Scheduled(cron = "0 51 17 * * ?")
    public void execute(){
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<List<String>> listCodes = RealCodeUtil.getCodesList(400, Arrays.asList(codeArray));
        latch = new CountDownLatch(listCodes.size());
        for (int i = 0; i < listCodes.size(); i++) {
            excutor.execute(new DownLoadThread(listCodes.get(i),restTemplate,latch,1));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //解析交易明细数据
        this.parseExcel();
    }

    private void parseExcel(){
        String directory = "E:\\excel\\stock\\capital\\"+ TimeUtil.dateFormat(TimeUtil.getFrontDay(new Date(), 1),"yyyyMMdd")+"\\";
        List<String> fileNames = FileUtil.getAllFileName(directory);
        for(String fileName:fileNames){
            //解析下载的excel文件
            List<ClinchDetail> list = FileUtil.readExcel(ClinchDetail.class,directory,fileName);
            //转换为CapitalInfo对象
            String code = fileName.substring(0,7);
            ClinchInfo clinchInfo = this.transformCapital(list,code);
            if (clinchInfo != null){
                try {
                    clinchService.insertClinch(clinchInfo);
                }catch (Exception e){
                    log.info("交易明细数据解析异常:{} Exception:{}",JSON.toJSONString(clinchInfo),e);
                }
            }
        }
    }

    private ClinchInfo transformCapital( List<ClinchDetail> list,String code){
        if (null == list || list.isEmpty()){
            log.info("没有交易明细记录:{}",code);
            return null;
        }
        double capitalIn = 0,capitalOut = 0,bigCapitalIn = 0,bigCapitalOut = 0;
        HashSet<Integer> set = new HashSet();
        for(ClinchDetail detail:list){
            set.add(detail.getClinchQuantity());
            boolean isBig =detail.getClinchSum() > 300000;
            switch (detail.getClinchNature()){
                case 0:
                    capitalOut = capitalOut+detail.getClinchPrice();
                    if (isBig){
                        bigCapitalOut = bigCapitalOut+detail.getClinchPrice();
                    }
                    break;
                case 1:
                    capitalIn = capitalIn + detail.getClinchPrice();
                    if (isBig){
                        bigCapitalIn = bigCapitalIn+detail.getClinchPrice();
                    }
                    break;
            }
        }
        double netInflow = BigDecimalUtil.sub(capitalIn,capitalOut,4);
        double bigNetInflow = BigDecimalUtil.sub(bigCapitalIn,bigCapitalOut,4);
        double netInflowPct = BigDecimalUtil.sub(netInflow,bigNetInflow,4);
        ClinchInfo clinchInfo = ClinchInfo.builder()
                .sotckCode(code)
                .dealTime(TimeUtil.dateFormat(TimeUtil.getFrontDay(new Date(), 1),"yyyy-MM-dd"))
                .capitalIn(capitalIn)
                .capitalOut(capitalOut)
                .netInflow(netInflow)
                .bigCapitalIn(bigCapitalIn)
                .bigCapitalOut(bigCapitalOut)
                .bigNetInflow(bigNetInflow)
                .netInflowPct(netInflowPct)
                .redoPct(BigDecimalUtil.div(set.size(),list.size(),4))
                .build();
        return clinchInfo;
    }
}
