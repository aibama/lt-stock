package com.lt.task.transform;

import com.alibaba.fastjson.JSON;
import com.lt.common.BigDecimalUtil;
import com.lt.common.RedisUtil;
import com.lt.entity.RealMarket;
import com.lt.service.BatchService;
import com.lt.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
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
    @Autowired
    RealMarketFilter realMarketFilter;
    private static ConcurrentHashMap<String,RealMarket> filterMap = new ConcurrentHashMap();

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
                if(values.length < 38)
                    continue;
                this.transform(values);
            }
        }

        /**
         * 数据转换并计算均值
         * @param values
         */
        private void transform(String[] values){
//            if (!values[2].equals("002945")){
//                return;
//            }
            String code = values[2];
            String time = values[30];
            double dealNum = Double.valueOf(values[36]);
            double dealRmb = Double.valueOf(values[37]);
            RealMarket realMarket = this.removeDuplicates(code,time,dealNum,dealRmb);
            if (null != realMarket){
                realMarket.setStockName(values[1]);
                realMarket.setStockCode(code);
                realMarket.setNowPrice(values[3]);
                realMarket.setClosePrice(values[4]);
                realMarket.setRose(values[32]);
                realMarket.setExchange(values[38]);
//                time = realMarket.getDealTime();
                this.isMinute(realMarket,time);
                log.info("1#1:{}",JSON.toJSONString(realMarket));
//                String realMarketJson = JSON.toJSONString(realMarket);
//                if(realMarketFilter.durationFilter(realMarket.getDuration())){
//                    return;
//                }
//                if(realMarketFilter.roseFilter(Double.valueOf(realMarket.getRose()))){
//                    String var = realMarket.getStockCode();
//                    redisUtil.lRemove(Constants.CODES,1,"sh"+var);
//                    redisUtil.lRemove(Constants.CODES,1,"sz"+var);
//                    return;
//                }
//                redisUtil.sZSet(Constants.REAL_MARKET_TF, realMarket.getStockCode(),realMarket.getDuration());
//                redisUtil.set(realMarket.getStockCode(),realMarketJson);
            }
        };

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
            //第一次加载不需要去重
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
                    .avgPriceOld(avg)
                    .dealTime(time)
                    .timeMinute(Long.valueOf(time.trim().replace(":","").substring(0,12)))
                    .build();
            filterMap.put(code,realMarket);
            return realMarket;
        }

        /**
         * 按时间去重
         * @param code
         * @param time
         * @param dealNum
         * @param dealRmb
         * @return
         */
        public RealMarket timeDuplicates(String code,String time,
                                         double dealNum,double dealRmb){
            RealMarket realMarket = filterMap.get(code);
            long oldTimeSign = Long.valueOf(realMarket.getDealTime());
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//            try {
//                Date date = dateFormat.parse(oldTimeSign);
//                long Time = date.getTime()+2000;
//                date.setTime(Time);
//                time = dateFormat.format(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

            if (Long.valueOf(time) - oldTimeSign <= 0){
                return null;
            }
            double dealNumSum = BigDecimalUtil.add(dealNum,realMarket.getDealNumSum());
            double dealRmbSum = BigDecimalUtil.add(dealRmb,realMarket.getDealRmbSum());
            realMarket.setDealNum(dealNum);
            realMarket.setDealRmb(dealRmb);
            realMarket.setDealNumSum(dealNumSum);
            realMarket.setDealRmbSum(dealRmbSum);
            realMarket.setVolamount(realMarket.getVolamount()+1);
            realMarket.setDealTime(time);
            filterMap.put(code,realMarket);
            return realMarket;
        }

        /**
         * 每分钟计算一次均价
         * @param realMarket
         * @param time
         */
        public void isMinute(RealMarket realMarket,String time){
            //每分钟计算一次均价
            long minute = Long.valueOf(time.substring(0,12));
            if ((minute - realMarket.getTimeMinute()) >= 1){
                double avg = this.calculateAvg(realMarket.getDealNumSum(),realMarket.getDealRmbSum());
                realMarket.setAvgPrice(avg);
                realMarket.setTimeMinute(minute);
                //计算均价持续时间
                if (avg - realMarket.getAvgPriceOld() != 0){
                    realMarket.setAvgPriceOld(avg);
                    realMarket.setDuration(0);
                }else {
                    realMarket.setDuration(realMarket.getDuration()+1);
                }
                //重置上一分钟的成交次数
                filterMap.get(realMarket.getStockCode()).setVolamount(0);
            }
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
        LocalTime.now().isAfter(LocalTime.of(15, 00, 00));
        System.out.println(spread("002925".hashCode()) == spread("002927".hashCode()));
        System.out.println(spread("002925".hashCode()));
        System.out.println(spread("002927".hashCode()));
        System.out.println("20191029100118".substring(0,12));
    }

    static final int spread(int h) {
        return (h ^ (h >>> 16)) & 0x7fffffff;
    }
}
