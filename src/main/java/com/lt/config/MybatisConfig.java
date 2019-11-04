package com.lt.config;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@MapperScans(value = {@MapperScan("com.lt.mapper")})
public class MybatisConfig {

    @Bean
    @Primary
    public MybatisProperties mybatisProperties(){
        MybatisProperties properties = new MybatisProperties();
        properties.setConfigLocation("classpath:mybatis/mybatis-config.xml");
        properties.setTypeAliasesPackage("com.lt.entity");
        properties.setMapperLocations(new String[]{"classpath:mapper/*.xml"});
        return properties;
    }
}
