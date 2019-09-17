package com.lt.task;

import com.lt.common.HttpClientUtil;
import com.lt.common.TimeUtil;
import com.lt.utils.StockCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gaijf
 * @description 实时行情数据获取
 * @date 2019/9/17
 */
@Slf4j
public class RealPriceTask {

    private static final List<String> params = new ArrayList<>();
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2,8,30, TimeUnit.SECONDS,new ArrayBlockingQueue<>(60));
    @PostConstruct
    public void init(){
        StockCodeUtil.getCodesStr(400,params);
        log.info("=================实时行情初始化完成==================");
    }

    @Scheduled(cron = "0/1 * * * * *")
    public void execute() throws ParseException {
        if (TimeUtil.isEffectiveDate("09:28:00","11:30:00","HH:mm:ss")
                || !TimeUtil.isEffectiveDate("12:59:59","15:00:00","HH:mm:ss")){
            for (int i = 0; i < params.size(); i++) {
                threadPool.execute(new RealThread(params.get(i)));
            }
            long count = threadPool.getTaskCount()-threadPool.getCompletedTaskCount();
            System.out.println("RealPriceTask:总数:"+threadPool.getTaskCount()+"完成:"+threadPool.getCompletedTaskCount()+"等待:"+count+"线程数量:"+threadPool.getPoolSize());
        }
    }

    private class RealThread implements Runnable {
        private String codes;

        public RealThread(String codes){
            this.codes = codes;
        }

        @Override
        public void run() {
            try {
                String result = HttpClientUtil.getRequest("http://qt.gtimg.cn/q="+codes);
            } catch (HttpException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
