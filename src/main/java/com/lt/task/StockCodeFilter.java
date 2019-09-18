package com.lt.task;

import com.lt.common.HttpClientUtil;
import com.lt.utils.Constants;
import com.lt.utils.RealCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

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

    public static final List<String> CODES = new ArrayList<>();

    @PostConstruct
    public void init(){
        execute();
        log.info("=================实时行情数据获取任务初始化完成==================");
    }

    @Scheduled(cron = "0 25 09 * * ?")//每天10:15运行 "0 15 10 * * ?"
    public void execute() {
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<String> listCodes = RealCodeUtil.getCodesStr(400,Arrays.asList(codeArray));
        for (int i = 0; i < listCodes.size(); i++) {
            try {
                String results = HttpClientUtil.getRequest("http://qt.gtimg.cn/q="+listCodes.get(i));
                StringTokenizer token = new StringTokenizer(results,";");
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
                    if (nowPrice == 0 || rose > 2 || rose < -1)
                        continue;
                    String code = values[0].trim().substring(2,10);
                    CODES.add(code);
                }
            } catch (Exception e) {
                e.printStackTrace();
            };
        }
    }
}
