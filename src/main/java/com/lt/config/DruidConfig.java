package com.lt.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DruidConfig {

    @Autowired
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource createDtasource(DataSource dataSource){
        DruidDataSource druidDataSource = (DruidDataSource)dataSource;
        druidDataSource.setProxyFilters(Lists.newArrayList(statFilter()));
        return dataSource;
    }

    @Bean
    public Filter statFilter(){
        StatFilter filter = new StatFilter();
        //定义多长时间为慢SQL
        filter.setSlowSqlMillis(5000);
        //是否记录日志
        filter.setLogSlowSql(true);
        //是否合并日志
        filter.setMergeSql(true);
        return filter;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        return new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
    }
}
