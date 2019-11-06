package com.lt.task.extract;

import com.lt.common.RedisUtil;
import com.lt.common.TimeUtil;
import com.lt.utils.Constants;
import com.lt.utils.RealCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gaijf
 * @description 实时行情数据获取
 * @date 2019/9/17
 * net stop redis;
 * net start redis;
 * 优化:1、redis关闭AOF和RDB存储功能
 */
@Slf4j
public class RealMarketExtract {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private RestTemplate restTemplate;
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2,50,2, TimeUnit.SECONDS,new ArrayBlockingQueue<>(50));

    @Scheduled(cron = "0/1 * * * * *")
    public void execute() throws ParseException {
        List<String> codes = RealCodeUtil.getCodesStr(400,redisUtil.lGet(Constants.CODES,0,-1));
        if (TimeUtil.isEffectiveDate("09:30:00","11:30:00","HH:mm:ss")
//                || TimeUtil.isEffectiveDate("12:59:59","15:00:00","HH:mm:ss")
            ){
            for (int i = 0; i < codes.size(); i++) {
                threadPool.execute(new RealThread(codes.get(i)));
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
            ResponseEntity<String> entity = null;
            try {
                entity = restTemplate.getForEntity("http://qt.gtimg.cn/q="+codes,String.class);
            } catch (ResourceAccessException e){
                for (int i = 0;i < 2;i++){
                    entity = restTemplate.getForEntity("http://qt.gtimg.cn/q="+codes,String.class);
                    if (null != entity)
                        break;
                }
            }catch (Exception e) {
                log.info("实时行情数据获取异常",e);
            }finally {
                if (null == entity || entity.getBody() == null){
                    log.info("实时行情数据获取失败:{}",codes);
                    return;
                }
                redisUtil.rPush(Constants.RAW_REAL_PRICE,entity.getBody());
            }
        }
    }
}
