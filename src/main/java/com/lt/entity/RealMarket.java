package com.lt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author gaijf
 * @description
 * @date 2019/9/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealMarket implements Serializable {

    //股票名称
    private String stockName;
    //股票代码
    private String stockCode;
    //当前价格
    private String nowPrice;
    //昨天收盘价
    private String closePrice;
    //开盘价
    private String openPrice;
    //平均价格
    private double avgPrice;
    //平均价格旧值
    private double avgPriceOld;
    //成交时间 秒
    private String dealTime;
    //成交时间 分
    private long timeMinute;
    //涨跌
    private String rose;
    //成交量
    private String dealNum;
    //总成交量
    private double dealNumSum;
    //成交额
    private String dealRmb;
    //总成交额
    private double dealRmbSum;
    //换手率
    private String exchange;
    //分钟内成交次数
    private int volamount;
    //均值持续时间
    private int duration;
}
