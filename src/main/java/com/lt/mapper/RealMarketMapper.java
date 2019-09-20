package com.lt.mapper;

import com.lt.entity.RealMarket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author gaijf
 * @description
 * @date 2019/9/20
 */
@Mapper
public interface RealMarketMapper {

    @Insert({"INSERT INTO lt_realmarket (stock_name,stock_code,now_price,close_price,open_price," +
            "avg_price,deal_time,time_minute,rose,deal_num,deal_rmb,exchange) values (#{stockName}," +
            "#{stockCode},#{nowPrice},#{closePrice},#{openPrice},#{avgPrice},#{dealTime},#{timeMinute},#{rose},#{dealNum},#{dealRmb},#{exchange})"})
    void insertRealMarket(RealMarket realMarket);
}
