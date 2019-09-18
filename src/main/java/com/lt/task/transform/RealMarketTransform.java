package com.lt.task.transform;

import com.lt.common.RedisUtil;
import com.lt.entity.RealMarket;
import com.lt.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
@Component
public class RealMarketTransform {

    @Autowired
    RedisUtil redisUtil;
    private static ConcurrentHashMap<String,String> filterMap = new ConcurrentHashMap();
    private static ConcurrentHashMap<String,RealMarket> avgPriceMap = new ConcurrentHashMap();

    @PostConstruct
    public void init(){
        new Thread(new PriceTransformThread()).start();
//        new Thread(new PriceTransformThread()).start();
        log.info("=================实时行情数据转换任务初始化完成==================");
    }

    private class PriceTransformThread implements Runnable{
        @Override
        public void run() {
            while (true){
                //阻塞式brpop，List中无数据时阻塞，参数0表示一直阻塞下去，直到List出现数据
                String results = redisUtil.lPop(Constants.RAW_REAL_PRICE,0);
                StringTokenizer token = new StringTokenizer(results,";");
                while(token.hasMoreTokens()){
                    String result = token.nextToken();
                    if (StringUtils.isEmpty(result.trim()))
                        continue;
                    String[] values = result.split("~");
//                    if (!values[2].equals("600468") || Double.valueOf(values[3]) == 0)
//                        continue;
                    String code = values[2];
                    String time = values[30];
                    String timeSign = code+time;
                    double dealNum = Double.valueOf(values[36])*100;
                    double dealRmb = Double.valueOf(values[37]);
                    System.out.println(values[32]+"==================");
                    RealMarket realMarket = this.calculateAvg( code, timeSign, dealNum, dealRmb);
//                    if (null != realMarket)
//                        System.out.println("~~~~~~~~~~"+JSON.toJSONString(realMarket));
//                    RealMarket realMarket= RealMarket.builder()
//                            .stockName(values[1])
//                            .stockCode(values[2])
//                            .nowPrice(values[3])
//                            .closePrice(values[4])
//                            .openPrice(values[5])
//                            .dealTime(values[30])
//                            .rose(values[32])
//                            .dealNum(values[36])
//                            .dealRmb(values[37])
//                            .exchange(values[38])
//                            .build();
                }
            }
        }

        /**
         * 均价计算
         * @param code
         * @param timeSign
         * @param dealNum
         * @param dealRmb
         */
        private RealMarket calculateAvg(String code,String timeSign,
                                  double dealNum,double dealRmb){
            RealMarket realMarket = null;
            //过滤掉重复数据，根据code和时间
            if (filterMap.containsKey(code)){
                String oldTimeSign = filterMap.get(code);
                if (!oldTimeSign.equals(timeSign)){
                    realMarket = avgPriceMap.get(oldTimeSign);
                    dealNum = dealNum+realMarket.getDealNumSum();
                    dealRmb = dealRmb+realMarket.getDealRmbSum();
                    double avg = dealRmb/dealNum;
                    realMarket.setDealNumSum(dealNum);
                    realMarket.setDealRmbSum(dealRmb);
                    realMarket.setAvgPrice(avg);
                    avgPriceMap.put(timeSign,realMarket);
                    filterMap.put(code,timeSign);
                }
            }else {
                double avg = dealRmb/dealNum;
                realMarket = realMarket.builder()
                        .dealNumSum(dealNum)
                        .dealRmbSum(dealRmb)
                        .avgPrice(avg)
                        .build();
                avgPriceMap.put(timeSign,realMarket);
                filterMap.put(code,timeSign);
            }
            return realMarket;
        }
    }

    public static void main(String[] args) {
        String s = "v_sh600470=1~六国化工~600470~4.95~4.98~5.01~99391~44594~54797~4.94~921~4.93~423~4.92~488~4.91~904~4.90~1838~4.95~91~4.96~1332~4.97~1655~4.98~2413~4.99~1385~14:53:08/4.95/33/B/16335/29755|14:52:59/4.95/9/S/4455/29741|14:52:56/4.95/24/S/11880/29735|14:52:47/4.95/17/B/8415/29717|14:52:41/4.95/87/B/43065/29706|14:52:38/4.94/4/S/1976/29699~20190918145316~-0.03~-0.60~5.02~4.94~4.95/99358/49475981~99391~4949~1.91~-4.47~~5.02~4.94~1.61~25.82~25.82~1.74~5.48~4.48~0.36~-2302~4.98~29.04~-4.27~~~1.31~4949.23~0.00~0~ ~GP-A~32.00~~0.00";
        String m = "v_sh600472=1~包头铝业~600472~0.00~0.00~0.00~0~0~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~~20190918145316~0.00~0.00~0.00~0.00~0.00/0/0~0~0~0.00~0.00~D~0.00~0.00~0.00~0.00~0.00~0.00~0.00~0.00~0.00~0~0.00~0.00~0.00~~~~0.00~0.00~0~ ~GP-A~~~0.00";
        String[] values = s.split("~");
        for (int i = 0;i < values.length;i++){
            System.out.println(values[i]);
        }
        String str = "hello,java,delphi,asp,php";
        StringTokenizer st=new StringTokenizer(str,",");
        while(st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
    }
}
