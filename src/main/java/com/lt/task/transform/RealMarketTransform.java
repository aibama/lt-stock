package com.lt.task.transform;

import com.alibaba.fastjson.JSON;
import com.lt.common.BigDecimalUtil;
import com.lt.common.RedisUtil;
import com.lt.entity.RealMarket;
import com.lt.service.BatchService;
import com.lt.utils.Constants;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaijf
 * @description
 * @date 2019/9/18
 */
@Slf4j
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
                }catch (RedisException e){
                    log.info("实时行情数据转换异常:{}",e.getMessage());
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
            String code = values[2];
            String time = values[30];
            String [] transaction = values[35].split("/");
            if(transaction.length < 3)
                return;
            double dealNum = Double.valueOf(transaction[1]);
            double dealRmb = Double.valueOf(transaction[2]);
            RealMarket realMarket = this.removeDuplicates(code,time,dealNum,dealRmb);
            if (null != realMarket){
                realMarket.setStockName(values[1]);
                realMarket.setStockCode(code);
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
                                        double dealRmb,double dealNum){
            RealMarket realMarket = null;
            //均价计算
            double avg = dealNum <= 0 ? 0:BigDecimalUtil.div(dealRmb,dealNum,2);
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
                //均价计算
                double avg = BigDecimalUtil.div(realMarket.getDealRmbSum(),realMarket.getDealNumSum(),2);
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
    }

    public static void main(String[] args) {
        String s = "v_sh601857=1~中国石油~601857~5.98~6.03~6.03~597371~220347~377024~5.98~40586~5.97~17203~5.96~11378~5.95~13217~5.94~5869~5.99~4487~6.00~20054~6.01~12349~6.02~12050~6.03~12213~~20191030153404~-0.05~-0.83~6.03~5.98~5.98/597371/358540585~597371~35854~0.04~20.43~~6.03~5.98~0.83~9682.94~10944.65~0.90~6.63~5.43~1.26~27102~6.00~19.26~20.81~~~0.52~35854.06~0.00~0~ ~GP-A~-15.06~~2.80~4.37~2.76";
//        s="v_sh600519=1~贵州茅台~600519~358.74~361.29~361.88~27705~12252~15453~358.75~8~358.74~4~358.72~7~358.71~6~358.70~5~358.77~3~358.78~2~358.79~16~358.80~4~358.86~1~14:59:59/358.75/5/S/179381/28600|14:59:56/358.75/1/S/35875/28594|14:59:53/358.75/1/S/35875/28588|14:59:50/358.75/1/S/35875/28579|14:59:47/358.75/4/B/143499/28574|14:59:41/358.72/4/S/143501/28562~20170221150553~-2.55~-0.71~362.43~357.18~358.75/27705/994112865~27705~99411~0.22~27.24~~362.43~357.18~1.45~4506.49~4506.49~6.57~397.42~325.16~0.86";
        String[] values = s.split("~");
        double dealNum = Double.valueOf(values[36]);
        double dealRmb = Double.valueOf(values[37]);
        System.out.println(dealNum+"========"+dealRmb);
        for (int i = 0;i < values.length;i++){
            System.out.println(i+"=========="+values[i]);
        }
//        String str = "hello,java,delphi,asp,php";
//        StringTokenizer st=new StringTokenizer(str,",");
//        while(st.hasMoreTokens()) {
//            System.out.println(st.nextToken());
//        }
        String m = "3.81/116979/44775721";
        String[] mm = m.split("/");
        System.out.println(mm.length);
    }

    static final int spread(int h) {
        return (h ^ (h >>> 16)) & 0x7fffffff;
    }
}
