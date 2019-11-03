package com.lt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaijf
 * @description
 * @date 2019/10/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapitalInfo {

    //股票代码
    private String stockCode;
    //股票名称
    private String stockName;
    //交易日期
    private String dealTime;
    //成交金额
    private double capitalSize;
    //上涨下跌百分比
    private double rose;
    //换手率
    private String exchange;
    //成交量
    private int voturnover;
    //流通市值
    private double circulationCap;
    //总市值
    private double mktCap;
}
