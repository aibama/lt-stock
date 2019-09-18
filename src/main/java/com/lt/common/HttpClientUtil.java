package com.lt.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/9/17
 */
@Slf4j
public class HttpClientUtil {

    // 发送GET请求
    public static String getRequest(String path) throws HttpException, URISyntaxException {
        return getRequest(path, null);
    }

    // 发送GET请求
    public static String getRequest(String path, List<NameValuePair> parametersBody) throws HttpException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(path);
        if (parametersBody != null)
            uriBuilder.setParameters(parametersBody);
        HttpGet get = new HttpGet(uriBuilder.build());
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(get);
            int code = response.getStatusLine().getStatusCode();
            if (code >= 400)
                throw new HttpException((new StringBuilder()).append("Could not access protected resource. Server returned http code: ").append(code).toString());
            return EntityUtils.toString(response.getEntity());
        }
        catch (ClientProtocolException e) {
            throw new HttpException("getRequest -- Client protocol exception!", e);
        }
        catch (IOException e) {
            throw new HttpException("getRequest -- IO error!", e);
        }
        finally {
            get.releaseConnection();
        }
    }

    // 发送POST请求（普通表单形式）
    public static String postForm(String path, List<NameValuePair> parametersBody) throws HttpException {
        HttpEntity entity = new UrlEncodedFormEntity(parametersBody, Charsets.UTF_8);
        return postRequest(path, "application/x-www-form-urlencoded", entity);
    }

    // 发送POST请求（JSON形式）
    public static String postJSON(String path, String json) throws HttpException {
        StringEntity entity = new StringEntity(json, Charsets.UTF_8);
        return postRequest(path, "application/json", entity);
    }

    // 发送POST请求
    public static String postRequest(String path, String mediaType, HttpEntity entity) throws HttpException {
        log.info("[postRequest] resourceUrl: {}", path);
        HttpPost post = new HttpPost(path);
        post.addHeader("Content-Type", mediaType);
        post.addHeader("Accept", "application/json");
        post.setEntity(entity);
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if (code >= 400)
                throw new HttpException(EntityUtils.toString(response.getEntity()));
            return EntityUtils.toString(response.getEntity());
        }
        catch (ClientProtocolException e) {
            throw new HttpException("postRequest -- Client protocol exception!", e);
        }
        catch (IOException e) {
            throw new HttpException("postRequest -- IO error!", e);
        }
        finally {
            post.releaseConnection();
        }
    }
}
