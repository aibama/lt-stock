package com.lt.task;

import com.lt.utils.Constants;
import com.lt.utils.RealCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author gaijf
 * @description
 * @date 2019/9/18
 */
@Slf4j
public class StockCodeFilter {

    @Autowired
    private RestTemplate restTemplate;

    public static final List<String> CODES = new ArrayList<>();

    @PostConstruct
    public void init(){
        execute();
    }

//    @Scheduled(cron = "0 26 09 * * ?")//每天10:15运行 "0 15 10 * * ?"
    public void execute() {
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<String> listCodes = RealCodeUtil.getCodesStr(400,Arrays.asList(codeArray));
        for (int i = 0; i < listCodes.size(); i++) {
            try {
                ResponseEntity<String> entity = restTemplate.getForEntity("http://qt.gtimg.cn/q="+listCodes.get(i),String.class);
                StringTokenizer token = new StringTokenizer(entity.getBody(),";");
                while(token.hasMoreTokens()){
                    String result = token.nextToken();
                    if (StringUtils.isEmpty(result.trim()))
                        continue;
                    String[] values = result.split("~");
                    if (values.length < 32){
                        log.info("股票编码:{} 执行结果:{}",values[2],result);
                        continue;
                    }
                    double nowPrice = Double.valueOf(values[3]);
                    double rose = Double.valueOf(values[32]);
                    double dealRmb =Double.valueOf(values[37]);
                    if (nowPrice == 0 || dealRmb == 0 || rose > 2 || rose < -1)
                        continue;
                    String code = values[0].trim().substring(2,10);
                    CODES.add(code);
                }
            } catch (Exception e) {
                e.printStackTrace();
            };
        }
        log.info("=================股票代码过滤任务完成==================");
    }
}
