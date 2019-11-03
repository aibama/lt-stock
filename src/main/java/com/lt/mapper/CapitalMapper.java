package com.lt.mapper;

import com.lt.entity.CapitalInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CapitalMapper {

    @Insert({"INSERT INTO lt_capital_info " +
            "(stock_code,stock_name,deal_time,capital_size" +
            ",rose,exchange,voturnover,circulation_cap,mkt_cap) values (" +
            "#{stockCode},#{stockName},#{dealTime},#{capitalSize}," +
            "#{rose},#{exchange},#{voturnover}," +
            "#{circulationCap},#{mktCap})"})
    void insertCapital(CapitalInfo capitalInfo);

    @Select({
            "select capital_flow,continue_day,rose_day from lt_capital_info"
    })
    CapitalInfo queryCapitalLast(String code);
}
