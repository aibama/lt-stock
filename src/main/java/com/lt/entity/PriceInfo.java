package com.lt.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author gaijf
 * @description 股票基本信息
 * @date 2019/11/1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceInfo implements Serializable {
    //股票代码
    @CsvBindByName(column = "股票代码")//是否可以为null
    private String stockCode;
    //股票名称
    @CsvBindByName(column = "名称")
    private String stockName;
    //交易日期
    @CsvBindByName(column = "日期")
    private String dealTime;
    //收盘价
    @CsvBindByName(column = "收盘价")
    private String closePrice;
    //开盘价
    @CsvBindByName(column = "开盘价")
    private String openPrice;
    //涨跌幅
    @CsvBindByName(column = "涨跌幅")
    private String rose;
    //换手率
    @CsvBindByName(column = "换手率")
    private String exchange;
    //成交量
    @CsvBindByName(column = "成交量")
    private int voturnover;
    //成交金额
    @CsvBindByName(column = "成交金额")
    private double vaturnover;
    //总市值
    @CsvBindByName(column = "总市值")
    private double tcap;
    //流通市值
    @CsvBindByName(column = "流通市值")
    private double mcap;
}
