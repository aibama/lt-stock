package com.lt.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/9/17
 */
public class StockCodeUtil {

    public static int getCodesStr(int threadSize,List<String> params){
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<String> list = Arrays.asList(codeArray);
        // 总数据条数
        int dataSize = list.size();
        // 线程数
        int threadNum = dataSize / threadSize + 1;
        // 定义标记,过滤threadNum为整数
        boolean special = dataSize % threadSize == 0;
        for (int i = 0; i < threadNum; i++) {
            if (i == threadNum - 1) {
                if (special) {
                    break;
                }
                params.add(String.join(",",list.subList(threadSize * i, dataSize)));
            } else {
                params.add(String.join(",",list.subList(threadSize * i, threadSize * (i + 1))));
            }
        }
        return threadNum;
    }

    public static int getCodesList(int threadSize,List<List<String>> params){
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<String> list = Arrays.asList(codeArray);
        // 总数据条数
        int dataSize = list.size();
        // 线程数
        int threadNum = dataSize / threadSize + 1;
        // 定义标记,过滤threadNum为整数
        boolean special = dataSize % threadSize == 0;
        for (int i = 0; i < threadNum; i++) {
            if (i == threadNum - 1) {
                if (special) {
                    break;
                }
                params.add(list.subList(threadSize * i, dataSize));
            } else {
                params.add(list.subList(threadSize * i, threadSize * (i + 1)));
            }
        }
        return threadNum;
    }
}
