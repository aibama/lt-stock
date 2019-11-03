package com.lt.service;

import com.lt.entity.CapitalInfo;
import com.lt.mapper.CapitalMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gaijf
 * @description
 * @date 2019/11/2
 */
@Slf4j
@Service
public class CapitalService {

    @Autowired
    private CapitalMapper mapper;

    public void insertCapital(CapitalInfo capitalInfo){
        mapper.insertCapital(capitalInfo);
    };

    public CapitalInfo queryCapitalLast(String code) {
        return mapper.queryCapitalLast(code);
    }
}
