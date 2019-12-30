package com.lt.http;


import com.lt.utils.Constants;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

/**
 * @author gaijf
 * @description
 * @date 2019/11/29
 */
public class ImageDownloadUtil {
    public static void main(String[] args) throws IOException {
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).setConnectionRequestTimeout(6000).setConnectTimeout(6000).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        String [] codeArray = Constants.STOCK_CODE.split(",");
        for (String code : codeArray){
            HttpGet httpGet = new HttpGet("http://image.sinajs.cn/newchart/min/n/"+code+".gif");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == 200) {
                String path = "E:/"+LocalDate.now() +"/"+code+".jpg";
                File file = new File(path);
                File fileParent = file.getParentFile();
                //判断是否存在
                if (!fileParent.exists()) {
                    //创建父目录文件
                    fileParent.mkdirs();
                }
                byte[] b = EntityUtils.toByteArray(response.getEntity());
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b);
                fos.close();
            }
        }
    }
}
