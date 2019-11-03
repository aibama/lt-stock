package com.lt.service;

import com.lt.entity.ClinchInfo;
import com.lt.mapper.ClinchMapper;
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
public class ClinchService {

    @Autowired
    ClinchMapper clinchMapper;

    public void insertClinch(ClinchInfo clinchInfo){
        clinchMapper.insertClinch(clinchInfo);
    };
}
