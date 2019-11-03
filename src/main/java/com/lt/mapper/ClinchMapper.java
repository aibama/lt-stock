package com.lt.mapper;

import com.lt.entity.ClinchInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author gaijf
 * @description
 * @date 2019/11/2
 */
@Mapper
public interface ClinchMapper {

    @Insert({
            "INSERT INTO lt_clinch_info (sotck_code,deal_time,capital_in,capital_out," +
                    "net_inflow,big_capital_in,big_capital_out,big_net_inflow,net_inflow_pct,redo_pct) " +
                    "values (#{sotckCode},#{dealTime},#{capitalIn},#{capitalOut},#{netInflow},#{bigCapitalIn},#{bigCapitalOut}," +
                    "#{bigNetInflow},#{netInflowPct},#{redoPct})"
    })
    void insertClinch(ClinchInfo clinchInfo);
}
