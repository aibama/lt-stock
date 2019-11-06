package com.lt.mapper;

import com.lt.common.page.PageParams;
import com.lt.entity.RealMarket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/9/20
 */
@Mapper
public interface RealMarketMapper {

    @Insert({"INSERT INTO lt_realmarket (stock_name,stock_code," +
            "avg_price,deal_date,deal_time,time_minute,rose,deal_num_sum,deal_rmb_sum,exchange,volamount,repeat_ratio) values (#{stockName}," +
            "#{stockCode},#{avgPrice},#{dealDate},#{dealTime},#{timeMinute}," +
            "#{rose},#{dealNumSum},#{dealRmbSum},#{exchange},#{volamount},#{repeatRatio})"})
    void insertRealMarket(RealMarket realMarket);

    int getMarketCount(RealMarket realMarket);

    List<RealMarket> getMarketList(@Param("realMarket") RealMarket realMarket,@Param("pageParams") PageParams pageParams);

    int queryBriefMarketCount(RealMarket realMarket);

    List<RealMarket> queryBriefMarketList(@Param("realMarket") RealMarket realMarket,@Param("pageParams") PageParams pageParams);
}
