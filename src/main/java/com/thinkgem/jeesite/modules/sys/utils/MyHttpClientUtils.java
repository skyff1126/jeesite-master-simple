package com.thinkgem.jeesite.modules.sys.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


/**
 * Created by Administrator on 2014/6/25.
 */
public class MyHttpClientUtils {
    private static HttpClient httpClient;

    @Value("${im_cms_url}")
    private String imCmsUrl;

    static {
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
        cm.setMaxTotal(100);
        httpClient = new DefaultHttpClient(cm);
    }


    public static String doPost(String url, Map<String, String> params, String encoding) {
        try {
            UrlEncodedFormEntity formEntity = buildUrlEncodedFormEntity(encoding, params);
            HttpPost httpPost = buildHttpPost(url, formEntity);
            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), encoding);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String doPost(String url, Map<String, String> params, Map<String,String> headerParams,String encoding) {
        try {
            UrlEncodedFormEntity formEntity = buildUrlEncodedFormEntity(encoding, params);
            HttpPost httpPost = buildHttpPost(url, formEntity);
            if(null != headerParams)
            {
                Set<String> keySet = headerParams.keySet();
                Iterator<String> iter =  keySet.iterator();
                while(iter.hasNext())
                {
                    String key = iter.next();
                    httpPost.addHeader(key, headerParams.get(key));
                }
            }

            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), encoding);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static String doGet(String url, String encoding) throws IOException {
        HttpGet httpGet = buildHttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        return EntityUtils.toString(response.getEntity(), encoding);
    }

    public static byte[] doGetPic(String url) throws IOException {
        HttpGet httpGet = buildHttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        return EntityUtils.toByteArray(response.getEntity());
    }

    public static String doGet(String url, Map<String, String> params, String encoding) throws IOException {
        HttpGet httpGet = buildHttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        return EntityUtils.toString(response.getEntity(), encoding);
    }

    private static HttpPost buildHttpPost(String url, HttpEntity entity) {
        HttpPost httpPost = new HttpPost(url);
        if (entity != null) {
            httpPost.setEntity(entity);
        }
        return httpPost;
    }

    private static HttpGet buildHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        return httpGet;
    }

    private static UrlEncodedFormEntity buildUrlEncodedFormEntity(String encoding, List<NameValuePair> nameValuePairs) throws UnsupportedEncodingException {
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        if (nameValuePairs != null) {
            for (NameValuePair nameValuePair : nameValuePairs) {
                formParams.add(nameValuePair);
            }
        }
        return new UrlEncodedFormEntity(formParams, encoding);
    }

    private static UrlEncodedFormEntity buildUrlEncodedFormEntity(String encoding, Map<String, String> nameValuePairs) throws UnsupportedEncodingException {
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        if (nameValuePairs != null) {
//            for (Map.Entry<String, String> nameValueEntry : nameValuePairs.entrySet()) {
//                formParams.add(new BasicNameValuePair(nameValueEntry.getKey(), nameValueEntry.getValue()));
//            }
            Set<String> keySet = nameValuePairs.keySet();
            for(Object obj:keySet){
                
                formParams.add(new BasicNameValuePair( obj.toString(), nameValuePairs.get(obj)));
            }
            
            
        }
        return new UrlEncodedFormEntity(formParams, encoding);
    }

    public static String postRequestBody(String url, String content, Charset charset) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            if (content != null && content.length() > 0) {
                httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
                StringEntity se = new StringEntity(content, HTTP.UTF_8);
                httpPost.setEntity(se);
            }
            HttpResponse httpResponse = httpClient.execute(httpPost);

            StringBuilder builder = new StringBuilder();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), charset));
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }
            result = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.getConnectionManager().shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
