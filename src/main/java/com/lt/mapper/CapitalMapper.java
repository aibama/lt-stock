package com.lt.mapper;

import com.lt.entity.CapitalInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CapitalMapper {

    @Insert({"INSER INTO capital_info " +
            "(stockCode,stockName,dealTime,capitalSize,capitalFlow,inBigBillNum,inBigBillRmb,outBigBillNum,outBigBillRmb" +
            ",continueDay,rose,roseDay,exchange,voturnover,circulationCap,mktCap) values (" +
            "#{stockCode},#{stockName},#{dealTime},#{capitalSize},#{capitalFlow},#{inBigBillNum},#{inBigBillRmb}," +
            "#{outBigBillNum},#{outBigBillRmb},#{continueDay},#{rose},#{roseDay},#{exchange},#{voturnover},#{}," +
            "#{circulationCap},#{mktCap})"})
    void insertCapital(CapitalInfo capitalInfo);
}
