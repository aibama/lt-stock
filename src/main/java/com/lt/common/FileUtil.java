package com.lt.common;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/11/3
 */
public class FileUtil {

    /**
     * 获取文件夹下的所有文件名
     * @param path
     */
    public static List<String> getAllFileName(String path) {
        List<String> list = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                list.add(tempList[i].getName());
            }
        }
        return list;
    }

    /**
     * 解析EXCEL文件
     * @param clazz
     * @param filePath
     * @param fileName
     * @param <T>
     * @return
     */
    public static <T> List<T> readExcel(Class<T> clazz, String filePath, String fileName){
        String fullPath = filePath+fileName;
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);//头部列
        List<T> list = ExcelImportUtil.importExcel(
                new File(fullPath),
                clazz, params);
        return list;
    }

    /**
     * 解析csv文件并转成bean
     * @param storagePath csv文件存放地址
     * @param clazz 类
     * @param <T> 泛型
     * @return 泛型bean集合
     */
    public static <T> List<T> getCsvData(String storagePath, Class<T> clazz) {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(storagePath), Charset.forName("GBK"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(clazz);
        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(in)
                .withSeparator(',')
                .withMappingStrategy(strategy).build();
        return csvToBean.parse();
    }
}
