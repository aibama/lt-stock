package com.lt.entity;

import com.opencsv.bean.CsvBindByName;
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
    //流出流入资金差额
    private double capitalFlow;
    //买入大单数量
    private int inBigBillNum;
    //买入大单金额
    private double inBigBillRmb;
    //卖出大单数量
    private int outBigBillNum;
    //卖出大单金额
    private double outBigBillRmb;
    //流出流入持续时间(单位：天)
    private int continueDay;
    //上涨下跌百分比
    private double rose;
    //持续上涨下跌天数
    private int roseDay;
    //换手率
    private String exchange;
    //成交量
    private int voturnover;
    //流通市值
    private double circulationCap;
    //总市值
    private double mktCap;
}
