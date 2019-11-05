package com.lt.task.download;

import com.lt.common.TimeUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author gaijf
 * @description
 * @date 2019/11/3
 */
public class DownLoadThread implements Runnable{
    private List<String> codes;
    private RestTemplate restTemplate;
    private CountDownLatch latch;
    private int stockSign;
    public DownLoadThread(List<String> codes,RestTemplate restTemplate,
                          CountDownLatch latch,int stockSign){
        this.codes=codes;
        this.restTemplate=restTemplate;
        this.latch=latch;
        this.stockSign=stockSign;
    }
    @Override
    public void run() {
        for (String code:codes) {
            code = code.startsWith("sh") ? code.replace("sh","0"):code.replace("sz","1");
            String [] paths = getPath(code,stockSign);
            downloadHttp(restTemplate,paths[0],paths[1],0);
        }
        latch.countDown();
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
                paths[0] = "http://quotes.money.163.com/service/chddata.html?code="+code+"&start="+TimeUtil.getUserDate("yyyyMMdd")+"&end="+TimeUtil.getUserDate("yyyyMMdd")+"&fields=TCLOSE;TOPEN;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
                break;
            case 1:
                String date = TimeUtil.dateFormat(TimeUtil.getFrontDay(new Date(), 1),"yyyyMMdd");
                paths[1] = "E:\\excel\\stock\\capital\\"+ date+"\\"+code+".xls";
                paths[0] = "http://quotes.money.163.com/cjmx/"+ LocalDate.now().getYear() +"/"+date+"/"+code+".xls";
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
}
