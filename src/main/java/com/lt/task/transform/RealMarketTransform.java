package com.lt.task.transform;

import com.alibaba.fastjson.JSON;
import com.lt.common.BigDecimalUtil;
import com.lt.common.RedisUtil;
import com.lt.common.TimeUtil;
import com.lt.entity.RealMarket;
import com.lt.service.BatchService;
import com.lt.service.RealMarketService;
import com.lt.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaijf
 * @description
 * @date 2019/9/18
 */
@Slf4j
@Component
public class RealMarketTransform {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    BatchService batchService;
    private List<RealMarket> list = new ArrayList<>(5000);
    private static ConcurrentHashMap<String,String> filterMap = new ConcurrentHashMap();
    private static ConcurrentHashMap<String,RealMarket> avgPriceMap = new ConcurrentHashMap();

    @PostConstruct
    public void init(){
        new Thread(new PriceTransformThread()).start();
        log.info("=================实时行情数据转换任务初始化完成==================");
    }

    private class PriceTransformThread implements Runnable{
        @Override
        public void run() {
            while (true){
                try {
                    //阻塞式brpop，List中无数据时阻塞，参数0表示一直阻塞下去，直到List出现数据
                    String results = redisUtil.lPop(Constants.RAW_REAL_PRICE,0);
                    if (LocalTime.now().isAfter(LocalTime.of(15, 00, 00)) && list.size() > 0){
                        batchService.batchRealMarket(list);
                        list.clear();
                    }
                    this.resultSplit(results);
                }catch (Exception e){
                    log.info("实时行情数据转换异常:{}",e);
                }
            }
        }

        /**
         * 按照特定的规则拆分数据
         * @return
         */
        public void resultSplit(String results){
            StringTokenizer token = new StringTokenizer(results,";");
            while(token.hasMoreTokens()){
                String result = token.nextToken();
                if (StringUtils.isEmpty(result.trim()))
                    continue;
                String[] values = result.split("~");
                if(values.length < 30)
                    continue;
                this.transform(values);
            }
        }

        /**
         * 数据转换并计算均值
         * @param values
         */
        private void transform(String[] values){
//            if (!values[2].equals("002334") || Double.valueOf(values[3]) == 0){
//                return;
//            }
            String code = values[2];
            String time = values[30];
            double dealNum = Double.valueOf(values[36]);
            double dealRmb = Double.valueOf(values[37]);
            RealMarket realMarket = this.removeDuplicates( code, time, dealNum, dealRmb);
            if (null != realMarket){
                realMarket.setStockName(values[1]);
                realMarket.setStockCode(code);
                realMarket.setNowPrice(values[3]);
                realMarket.setClosePrice(values[4]);
                realMarket.setDealTime(values[30]);
                realMarket.setRose(values[32]);
                realMarket.setExchange(values[38]);
//                log.info("1#1{}1#1", JSON.toJSONString(realMarket));
            }
        };

        /**
         * 持久化到mysql
         * @param realMarket
         */
        public void persistence(RealMarket realMarket){
            if (list.size() <= 5000){
                list.add(realMarket);
            }else {
                batchService.batchRealMarket(list);
                list.clear();
            }
        }

        /**
         * 去除重复数据
         * @param code
         * @param time
         * @param dealNum
         * @param dealRmb
         * @return
         */
        public RealMarket removeDuplicates(String code,String time,
                                     double dealNum,double dealRmb){
            //判断是否需要去重
            if(filterMap.containsKey(code))
                return timeDuplicates( code, time,dealNum, dealRmb);
            return notDuplicates( code,time,dealNum,dealRmb);
        }

        /**
         * 不需要去重
         * @param code
         * @param time
         * @param dealNum
         * @param dealRmb
         * @return
         */
        public RealMarket notDuplicates(String code,String time,
                                        double dealNum,double dealRmb){
            RealMarket realMarket = null;
            double avg = this.calculateAvg(dealNum,dealRmb);
            realMarket = realMarket.builder()
                    .dealNumSum(dealNum)
                    .dealRmbSum(dealRmb)
                    .avgPrice(avg)
                    .timeMinute(Long.valueOf(time.trim().replace(":","").substring(0,12)))
                    .build();
            avgPriceMap.put(time,realMarket);
            filterMap.put(code,time);
            return realMarket;
        }

        /**
         * 按时间去重后计算
         * @param code
         * @param time
         * @param dealNum
         * @param dealRmb
         * @return
         */
        public RealMarket timeDuplicates(String code,String time,
                                         double dealNum,double dealRmb){
            RealMarket realMarket = null;
            String oldTimeSign = filterMap.get(code);
            if (!oldTimeSign.equals(time)){
                realMarket = avgPriceMap.get(oldTimeSign);
                dealNum = BigDecimalUtil.add(dealNum,realMarket.getDealNumSum());
                dealRmb = BigDecimalUtil.add(dealRmb,realMarket.getDealRmbSum());
                realMarket.setDealNumSum(dealNum);
                realMarket.setDealRmbSum(dealRmb);
                //每分钟计算一次均价
                long minute = Long.valueOf(time.trim().replace(":","").substring(0,12));
                if ((minute - realMarket.getTimeMinute()) == 1){
                    double avg = this.calculateAvg(dealNum,dealRmb);
                    realMarket.setAvgPrice(avg);
                    realMarket.setTimeMinute(minute);
//                    log.info("1#1{}1#1", JSON.toJSONString(realMarket));
                }
                avgPriceMap.put(time,realMarket);
                filterMap.put(code,time);
            }
            return realMarket;
        }

        /**
         * 均价计算
         * @param dealNum
         * @param dealRmb
         */
        private double calculateAvg(double dealNum,double dealRmb){
            dealNum = BigDecimalUtil.mul(dealNum,100);
            dealRmb = BigDecimalUtil.mul(dealRmb,10000);
            double avg = BigDecimalUtil.div(dealRmb,dealNum,2);
            return avg;
        }
    }

    public static void main(String[] args) {
//        String s = "v_sh600470=1~六国化工~600470~4.95~4.98~5.01~99391~44594~54797~4.94~921~4.93~423~4.92~488~4.91~904~4.90~1838~4.95~91~4.96~1332~4.97~1655~4.98~2413~4.99~1385~14:53:08/4.95/33/B/16335/29755|14:52:59/4.95/9/S/4455/29741|14:52:56/4.95/24/S/11880/29735|14:52:47/4.95/17/B/8415/29717|14:52:41/4.95/87/B/43065/29706|14:52:38/4.94/4/S/1976/29699~20190918145316~-0.03~-0.60~5.02~4.94~4.95/99358/49475981~99391~4949~1.91~-4.47~~5.02~4.94~1.61~25.82~25.82~1.74~5.48~4.48~0.36~-2302~4.98~29.04~-4.27~~~1.31~4949.23~0.00~0~ ~GP-A~32.00~~0.00";
//        String m = "v_sh600472=1~包头铝业~600472~0.00~0.00~0.00~0~0~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~~20190918145316~0.00~0.00~0.00~0.00~0.00/0/0~0~0~0.00~0.00~D~0.00~0.00~0.00~0.00~0.00~0.00~0.00~0.00~0.00~0~0.00~0.00~0.00~~~~0.00~0.00~0~ ~GP-A~~~0.00";
//        String[] values = s.split("~");
//        for (int i = 0;i < values.length;i++){
//            System.out.println(values[i]);
//        }
//        String str = "hello,java,delphi,asp,php";
//        StringTokenizer st=new StringTokenizer(str,",");
//        while(st.hasMoreTokens()) {
//            System.out.println(st.nextToken());
//        }
    }
}
