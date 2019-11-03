package com.lt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author gaijf
 * @description
 * @date 2019/11/2
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClinchInfo implements Serializable {
    private String sotckCode;
    private String dealTime;
    //流入资金
    private double capitalIn;
    //流出资金
    private double capitalOut;
    //资金净流入与净流出差值
    private double netInflow;
    //大单流入资金
    private double bigCapitalIn;
    //大单流出资金
    private double bigCapitalOut;
    //大单净流入与净流出差值
    private double bigNetInflow;
    //大单净流入
    private double netInflowPct;
    //成交订单重复占比
    private double redoPct;
}
