package com.lt.http;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lt.common.BigDecimalUtil;
import com.lt.common.FileUtil;
import com.lt.common.TimeUtil;
import com.lt.entity.CapitalInfo;
import com.lt.entity.ClinchDetail;
import com.lt.entity.ClinchInfo;
import com.lt.entity.PriceInfo;
import com.lt.service.CapitalService;
import com.lt.service.ClinchService;
import com.lt.utils.Constants;
import com.lt.utils.RealCodeUtil;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author gaijf
 * @description
 * @date 2019/9/19
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpTest {

    @Autowired
    CapitalService capitalService;
    @Autowired
    ClinchService clinchService;
    @Autowired
    private RestTemplate restTemplate;
    private CountDownLatch latch = null;
    private static final ThreadPoolExecutor excutor = new ThreadPoolExecutor(2,8,20,TimeUnit.SECONDS,new LinkedBlockingDeque<>(3000));

    @Test
    public void startTest() throws JSONException, IOException {
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<String> listCodes = RealCodeUtil.getCodesStr(400,Arrays.asList(codeArray));
        for (int i = 0; i < listCodes.size(); i++) {
            ResponseEntity<String> entity = restTemplate.getForEntity("http://qt.gtimg.cn/q="+listCodes.get(i),String.class);
            StringTokenizer token = new StringTokenizer(entity.getBody(),";");
            while(token.hasMoreTokens()){
                String result = token.nextToken();
                if (StringUtils.isEmpty(result.trim()))
                    continue;
                log.info("##{}",result);
            }
        }
        //http://quotes.money.163.com/service/chddata.html?code=1002196&start=20191031&end=20191031&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP
    }

    @Test
    public void httpDownload(){
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<List<String>> listCodes = RealCodeUtil.getCodesList(1000,Arrays.asList(codeArray));
        latch = new CountDownLatch(listCodes.size());
        for (int i = 0; i < listCodes.size(); i++) {
            excutor.execute(new DownLoadThread(listCodes.get(i),restTemplate,latch));
        }
        /**
         * 遇到的问题：由于主线程结束，导致创建的线程未正常执行完成就被动结束
         */
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class DownLoadThread implements Runnable{
        private List<String> codes;
        private RestTemplate restTemplate;
        private CountDownLatch latch;
        public DownLoadThread(List<String> codes,RestTemplate restTemplate,CountDownLatch latch){
            this.codes=codes;
            this.restTemplate=restTemplate;
            this.latch=latch;
        }
        @Override
        public void run() {
            for (String code:codes) {
                code = code.startsWith("sh") ? code.replace("sh","0"):code.replace("sz","1");
                String url = "http://quotes.money.163.com/cjmx/2019/20191101/"+code+".xls";
                String storagePath = "E:\\excel\\stock\\capital\\20191101\\"+code+".xls";
                HttpTest.downloadHttp(restTemplate,url,storagePath,0);
//                String [] paths = HttpTest.getPath(code,0);
//                HttpTest.downloadHttp(restTemplate,paths[0],paths[1],0);
            }
            latch.countDown();
        }
    }

    /**
     * 获取存储路径0交易基本信息1交易明细
     * @param code
     * @param sign
     * @return
     */
    public static String[] getPath(String code,int sign){
        String [] paths = new String[2];
        switch (sign){
            case 0:
                paths[1] = "E:\\excel\\stock\\price\\"+code+".csv";
                paths[0] = "http://quotes.money.163.com/service/chddata.html?code="+code+"&start=20191028&end=20191101&fields=TCLOSE;TOPEN;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
                break;
            case 1:
                paths[1] = "E:\\excel\\stock\\capital\\"+TimeUtil.getUserDate("yyyyMMdd")+"\\"+code+".xls";
                paths[0] = "http://quotes.money.163.com/cjmx/2019/"+TimeUtil.getUserDate("yyyyMMdd")+"/"+code+".xls";
                break;
        }
        return paths;
    }

    public static void downloadHttp(RestTemplate restTemplate,String url,String storagePath,int next){
        ResponseEntity<byte[]> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Resource> httpEntity = new HttpEntity<Resource>(headers);
            response = restTemplate.exchange(url, HttpMethod.GET,
                    httpEntity, byte[].class);
            File file = new File(storagePath);
            File fileParent = file.getParentFile();
            //判断是否存在
            if (!fileParent.exists()) {
                //创建父目录文件
                fileParent.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(response.getBody());
            fos.flush();
            fos.close();
        } catch (HttpClientErrorException e) {
            if(next < 5){
                downloadHttp(restTemplate,url,storagePath,++next);
            }else {
                System.out.println(url);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void download(){
        //交易明细
//        String storagePath = "E:\\excel\\stock\\capital\\20191030\\0601988.xls";
//        String url = "http://quotes.money.163.com/cjmx/2019/20191030/0601988.xls";
        //股票基本信息
        String storagePath = "E:\\excel\\stock\\price\\1002196.csv";
        String url = "http://quotes.money.163.com/service/chddata.html?code=1002196&start=20191025&end=20191031&fields=TCLOSE;TOPEN;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
        HttpTest.downloadHttp(restTemplate,url,storagePath,0);
    }

    @Test
    public void loadPriceFile(){
        String directory = "E:\\excel\\stock\\price\\";
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

    @Test
    public void loadCapitalFile(){
        List<String> list = new ArrayList<>();
        list.add("2019-10-28");
        list.add("2019-10-29");
        list.add("2019-10-30");
        list.add("2019-10-31");
        list.add("2019-11-01");
        String directory = "E:\\excel\\stock\\capital\\20191101\\";
        List<String> fileNames = FileUtil.getAllFileName(directory);
        loadFile(fileNames,directory);
    }

    public void loadFile(List<String> fileNames,String directory){
        for(String fileName:fileNames){
            //解析下载的excel文件
            List<ClinchDetail> list = FileUtil.readExcel(ClinchDetail.class,directory,fileName);
            //转换为CapitalInfo对象
            String code = fileName.substring(0,7);
            ClinchInfo clinchInfo = transformCapital(list,code);
            if (clinchInfo != null){
                try {
                    clinchService.insertClinch(clinchInfo);
                }catch (Exception e){
                    System.out.println(JSON.toJSONString(clinchInfo));
                    e.printStackTrace();
                }
            }
        }
    }
    public static ClinchInfo transformCapital( List<ClinchDetail> list,String code){
        if (null == list || list.isEmpty()){
            log.info("==============="+code);
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
                .dealTime("2019-11-01")
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

    public static CapitalInfo resultSplit(String result){
        if (StringUtils.isEmpty(result.trim()))
            return null;
        String[] values = result.split("~");
        if(values.length < 38)
            return null;
        return transform(values);
    }

    public static CapitalInfo transform(String[] values){
        CapitalInfo capitalInfo = CapitalInfo.builder()
                .stockName(values[1])
                .rose(Double.valueOf(values[32]))
                .exchange(values[38])
                .build();
        return capitalInfo;
    }

    public static void main(String[] args) {
        String code = "1002779";
        code=code.replaceFirst("1","sz");
        System.out.println(code);
        Date date = new Date();
        String time = String.format("%tF%n",date);
        TimeUtil.getUserDate("yyyyMMdd");
        System.out.println(TimeUtil.getUserDate("yyyyMMdd"));
    }
}
