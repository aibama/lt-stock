package com.lt.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author gaijf
 * @description
 * @date 2019/9/19
 */
@Configuration
public class RestTemplateConfig {

//--------------------------------------http连接池方式--------------------------------------
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(httpRequestFactory());
    }

    /**
     * 可以支持的客户端
     * 基于 JDK HttpURLConnection 的 SimpleClientHttpRequestFactory，默认。
     * 基于 Apache HttpComponents Client 的 HttpComponentsClientHttpRequestFactory
     * 基于 OkHttp3的OkHttpClientHttpRequestFactory。
     * 基于 Netty4 的 Netty4ClientHttpRequestFactory。
     * 其中HttpURLConnection 和 HttpClient 为原生的网络访问类，OkHttp3采用了 OkHttp3的框架，Netty4 采用了Netty框架。
     * @return
     */
    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(100);
        connectionManager.setValidateAfterInactivity(2000);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(65000) //服务器返回数据(response)的时间，超过抛出read timeout
                .setConnectTimeout(5000) //连接上服务器(握手成功)的时间，超出抛出connect timeout
                .setConnectionRequestTimeout(1000)//从连接池中获取连接的超时时间，超时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }
}
