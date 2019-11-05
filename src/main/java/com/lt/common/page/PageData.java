package com.lt.common.page;

import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/11/5
 */
public class PageData<T> {
    private Integer code=200;
    //总记录数量
    private Integer totals;

    private List<T> list;

    public static PageData build(Integer code,Integer totals,List<?> list){
        if (totals == null) {
            totals = 0;
        }
        return new PageData(code, totals,list);
    }

    public PageData (Integer code,Integer totals,List<T> list){
        this.code = code;
        this.totals = totals;
        this.list = list;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getTotals() {
        return totals;
    }

    public void setTotals(Integer totals) {
        this.totals = totals;
    }

    public List <T> getList() {
        return list;
    }

    public void setList(List <T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageDataResult{" +
                "code=" + code +
                ", totals=" + totals +
                ", list=" + list +
                '}';
    }
}
