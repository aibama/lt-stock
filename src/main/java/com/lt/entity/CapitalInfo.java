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
    //资金大小
    private double capitalSize;
    //资金流向0流出1流入
    private int capitalFlow;
    //买入大单数量
    private int inBigBill;
    //卖出大单数量
    private int outBigBill;
    //流出流入持续时间(单位：天)
    private int continueDay;
    //上涨下跌百分比
    private double rose;
    //持续上涨下跌天数
    private int roseDay;
    //换手率
    private String exchange;
}
