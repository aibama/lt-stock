package com.lt.task;

import com.lt.common.HttpClientUtil;
import com.lt.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/9/18
 */
@Slf4j
@Component
public class StockCodeFilter {

    private static final List<String> CODES = new ArrayList<>();

    @Scheduled(cron = "0 25 09 * * ?")//每天10:15运行 "0 15 10 * * ?"
    public void execute() throws ParseException {
        String [] codeArray = Constants.STOCK_CODE.split(",");
        for (int i = 0; i < codeArray.length; i++) {
            String code = codeArray[i];
            try {
                String result = HttpClientUtil.getRequest("http://qt.gtimg.cn/q="+code);
                if (StringUtils.isEmpty(result.trim()))
                    continue;
                String[] values = result.split("~");
                double nowPrice = Double.valueOf(values[3]);
                double rose = Double.valueOf(values[32]);
                if (nowPrice == 0 || rose > 2 || rose < -1)
                    continue;
                CODES.add(code);
            } catch (Exception e) {
                e.printStackTrace();
            };
        }
    }
}
