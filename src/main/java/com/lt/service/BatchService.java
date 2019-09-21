package com.lt.service;

import com.lt.entity.RealMarket;
import com.lt.mapper.RealMarketMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BatchService {

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Transactional(rollbackFor = Exception.class)
    public void batchRealMarket(List<RealMarket> list){
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        RealMarketMapper mapper = sqlSession.getMapper(RealMarketMapper.class);
        for (RealMarket realMarket: list) {
            mapper.insertRealMarket(realMarket);
        }
        sqlSession.flushStatements();
    };
}
