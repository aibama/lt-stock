package com.lt.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gaijf
 * @description 交易明细
 * @date 2019/10/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ExcelTarget("clinchDetail")
public class ClinchDetail implements Serializable {

    private Integer id;
    @Excel(name = "成交时间", format = "HH:mm" )
    private Date clinchTime;
    @Excel(name = "成交价")
    private Double clinchPrice;
    @Excel(name = "价格变动")
    private Double clinchChange;
    @Excel(name = "成交量（手）")
    private Integer clinchQuantity;
    @Excel(name = "成交额（元）")
    private Double clinchSum;
    @Excel(name = "性质", replace = { "卖盘_0", "买盘_1","中性盘_2" })
    private Integer clinchNature;
}
