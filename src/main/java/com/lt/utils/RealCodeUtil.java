package com.lt.utils;

import java.util.ArrayList;
import java.util.List;

public class RealCodeUtil {

    public static List<String> getCodesStr(int splitSize, List<String> codes){
        List<String> list = new ArrayList<>();
        // 总数据条数
        int dataSize = codes.size();
        // 线程数
        int paragraphSize = dataSize / splitSize + 1;
        // 定义标记,过滤threadNum为整数
        boolean special = dataSize % splitSize == 0;
        for (int i = 0; i < paragraphSize; i++) {
            if (i == paragraphSize - 1) {
                if (special) {
                    break;
                }
                list.add(String.join(",",codes.subList(splitSize * i, dataSize)));
            } else {
                list.add(String.join(",",codes.subList(splitSize * i, splitSize * (i + 1))));
            }
        }
        return list;
    }

    public static List<List<String>> getCodesList(int splitSize,List<String> codes){
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<List<String>> list = new ArrayList<>();;
        // 总数据条数
        int dataSize = codes.size();
        // 线程数
        int paragraphSize = dataSize / splitSize + 1;
        // 定义标记,过滤threadNum为整数
        boolean special = dataSize % splitSize == 0;
        for (int i = 0; i < paragraphSize; i++) {
            if (i == paragraphSize - 1) {
                if (special) {
                    break;
                }
                list.add(codes.subList(splitSize * i, dataSize));
            } else {
                list.add(codes.subList(splitSize * i, splitSize * (i + 1)));
            }
        }
        return list;
    }
}
