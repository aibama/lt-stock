package com.lt.http;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.druid.wall.violation.ErrorCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lt.common.TimeUtil;
import com.lt.entity.CapitalInfo;
import com.lt.entity.ClinchDetail;
import com.lt.entity.PriceInfo;
import com.lt.entity.RealMarket;
import com.lt.log.LogParse;
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
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
    private RestTemplate restTemplate;
    private CountDownLatch latch = null;
    private static final ThreadPoolExecutor excutor = new ThreadPoolExecutor(2,8,20,TimeUnit.SECONDS,new LinkedBlockingDeque<>(3000));
    private static final ConcurrentMap<String,List<PriceInfo>> mapPrices = new ConcurrentHashMap();

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
//                String url = "http://quotes.money.163.com/cjmx/2019/20191031/"+code+".xls";
//                String storagePath = "E:\\excel\\stock\\capital\\20191031\\"+code+".xls";
//                HttpTest.downloadHttp(restTemplate,url,storagePath,0);
                String [] paths = HttpTest.getPath(code,0);
                HttpTest.downloadHttp(restTemplate,paths[0],paths[1],0);
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
                paths[0] = "http://quotes.money.163.com/service/chddata.html?code="+code+"&start=20191025&end=20191031&fields=TCLOSE;TOPEN;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
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
        //http://quotes.money.163.com/service/chddata.html?code=1002196&start=20191031&end=20191031&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP
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
        List<String> fileNames = getAllFileName(directory);
        for (String str:fileNames){
            String storagePath = directory+str;
            List<PriceInfo> csvData = getCsvData(storagePath, PriceInfo.class);
            mapPrices.put(str.substring(0,8),csvData);
        }
    }

    /**
     * 解析csv文件并转成bean
     * @param storagePath csv文件存放地址
     * @param clazz 类
     * @param <T> 泛型
     * @return 泛型bean集合
     */
    public <T> List<T> getCsvData(String storagePath, Class<T> clazz) {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(storagePath),Charset.forName("GBK"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(clazz);
        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(in)
                .withSeparator(',')
                .withMappingStrategy(strategy).build();
        return csvToBean.parse();
    }

    @Test
    public void loadCapitalFile(){
        this.loadPriceFile();
        List<String> list = new ArrayList<>();
        list.add("2019-10-25");
        list.add("2019-10-28");
        list.add("2019-10-29");
        list.add("2019-10-30");
        list.add("2019-10-31");
        String directory = "E:\\excel\\stock\\capital\\20191025\\";
        List<String> fileNames = getAllFileName(directory);
        loadFile(fileNames,directory);
    }

    public void loadFile(List<String> fileNames,String directory){
        latch = new CountDownLatch(fileNames.size());
        for(String name:fileNames){
            excutor.execute(new ReadThread(directory,name,latch));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class ReadThread implements Runnable{
        private String filePath;
        private String fileName;
        private CountDownLatch latch;
        public ReadThread(String filePath,String fileName,CountDownLatch latch){
            this.filePath = filePath;
            this.fileName = fileName;
            this.latch = latch;
        }
        @Override
        public void run() {
            //解析下载的excel文件
            List<ClinchDetail> list = HttpTest.readExcel(ClinchDetail.class,filePath,fileName);
            //转换为CapitalInfo对象
            String code = fileName.substring(0,8);
            List<PriceInfo> priceInfos = mapPrices.get(code);
            code = code.startsWith("0") ? code.replaceFirst("0","sh"):code.replaceFirst("1","sz");
            CapitalInfo capitalInfo = transformCapital( list,code);
            if (null != priceInfos){
                for(PriceInfo info:priceInfos){
                    if (info.getDealTime().equals("2019-10-25")){
                        capitalInfo.setCapitalSize(info.getVaturnover());
                        capitalInfo.setVoturnover(info.getVoturnover());
                        capitalInfo.setCirculationCap(info.getMcap());
                        capitalInfo.setMktCap(info.getTcap());
                        capitalInfo.setExchange(info.getExchange());
                        capitalInfo.setStockName(info.getStockName());
                        capitalInfo.setDealTime(info.getDealTime());
                        double rose = info.getRose().equals("None") ? 0:Double.valueOf(info.getRose());
                        capitalInfo.setRose(rose);
                    }
                }
            }
            System.out.println(JSON.toJSONString(capitalInfo));
            latch.countDown();
        }
    }

    public static <T> List<T> readExcel(Class<T> clazz, String filePath, String fileName){
        String fullPath = filePath+fileName;
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        List<T> list = ExcelImportUtil.importExcel(
                new File(fullPath),
                clazz, params);
        return list;
    }

    public static CapitalInfo transformCapital( List<ClinchDetail> list,String code){
        //资金大小
        AtomicReference<Double> capitalSize = new AtomicReference<>((double) 0);
        AtomicInteger inBigBill = new AtomicInteger();
        AtomicReference<Double> inBigBillRmb = new AtomicReference<>((double) 0);
        AtomicInteger outBigBill = new AtomicInteger();
        AtomicReference<Double> outBigBillRmb = new AtomicReference<>((double) 0);
        list.stream().forEach(o ->{
            boolean isBig =o.getClinchSum() > 300000;
            switch (o.getClinchNature()){
                case 0:
                    capitalSize.set(capitalSize.get() - o.getClinchPrice());
                    if (isBig){
                        outBigBill.getAndIncrement();
                        inBigBillRmb.set(inBigBillRmb.get() + o.getClinchPrice());
                    }
                    break;
                case 1:
                    capitalSize.set(capitalSize.get() + o.getClinchPrice());
                    if (isBig){
                        inBigBill.getAndIncrement();
                        outBigBillRmb.set(outBigBillRmb.get() + o.getClinchPrice());
                    }
                    break;
            }
        });
        CapitalInfo capitalInfo = CapitalInfo.builder()
                .stockCode(code)
                .capitalFlow(capitalSize.get())
                .inBigBillNum(inBigBill.get())
                .inBigBillRmb(inBigBillRmb.get())
                .outBigBillNum(outBigBill.get())
                .outBigBillRmb(outBigBillRmb.get())
                .build();
        return capitalInfo;
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

    /**
     * 获取文件夹下的所有文件名
     * @param path
     */
    public static List<String> getAllFileName(String path) {
        List<String> list = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                list.add(tempList[i].getName());
            }
        }
        return list;
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
