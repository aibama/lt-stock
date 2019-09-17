package com.lt.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/9/17
 */
public class StockCodeUtil {

    public static int getCodesStr(int splitSize,List<String> params){
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<String> list = Arrays.asList(codeArray);
        // 总数据条数
        int dataSize = list.size();
        // 线程数
        int paragraphSize = dataSize / splitSize + 1;
        // 定义标记,过滤threadNum为整数
        boolean special = dataSize % splitSize == 0;
        for (int i = 0; i < paragraphSize; i++) {
            if (i == paragraphSize - 1) {
                if (special) {
                    break;
                }
                params.add(String.join(",",list.subList(splitSize * i, dataSize)));
            } else {
                params.add(String.join(",",list.subList(splitSize * i, splitSize * (i + 1))));
            }
        }
        return paragraphSize;
    }

    public static int getCodesList(int splitSize,List<List<String>> params){
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<String> list = Arrays.asList(codeArray);
        // 总数据条数
        int dataSize = list.size();
        // 线程数
        int paragraphSize = dataSize / splitSize + 1;
        // 定义标记,过滤threadNum为整数
        boolean special = dataSize % splitSize == 0;
        for (int i = 0; i < paragraphSize; i++) {
            if (i == paragraphSize - 1) {
                if (special) {
                    break;
                }
                params.add(list.subList(splitSize * i, dataSize));
            } else {
                params.add(list.subList(splitSize * i, splitSize * (i + 1)));
            }
        }
        return paragraphSize;
    }
}
