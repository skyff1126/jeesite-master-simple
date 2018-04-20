package com.thinkgem.jeesite.modules.sys.utils;

import com.thinkgem.jeesite.common.utils.DateUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasServiceUtil {

    public static String getServiceTicket(String server, String ticketGrantingTicket, String service) {
        if (StringUtils.isBlank(ticketGrantingTicket))
            return null;

        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(server + "/" + ticketGrantingTicket);
        NameValuePair simcard = new NameValuePair("service", service);
        post.setRequestBody(new NameValuePair[]{simcard});
        try {
            client.executeMethod(post);
            String response = post.getResponseBodyAsString();
            switch (post.getStatusCode()) {
                case 200:
                    return response;
                default:
                    System.out.println(("Invalid response code " + (post.getStatusCode()) + " from CAS server!"));
                    System.out.println("Response (1k): "
                            + response.substring(0,
                            Math.min(1024, response.length())));
                    break;
            }
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    public static String getTicketGrantingTicket(String server, String username, String password) {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(server);
        NameValuePair simcard1 = new NameValuePair("username", username);
        NameValuePair simcard2 = new NameValuePair("password", password);
        post.setRequestBody(new NameValuePair[]{simcard1, simcard2});
        try {
            System.out.println("errorCount:" + errorCount);
            client.executeMethod(post);
            String response = post.getResponseBodyAsString();
            switch (post.getStatusCode()) {
                case 201:
                    /*Header[] headers = post.getRequestHeaders();
                    for(Header header : headers)
                    {
                        if(header.getName().toLowerCase().equals("location"))
                        {
                            return header.getValue();
                        }
                    }*/
                    Matcher matcher = Pattern.compile(".*action=\".*//*(.*?)\".*")
                            .matcher(response);
                    if (matcher.matches())
                        return server + "/" + matcher.group(1);
                    System.out.println("Successful ticket granting request, but no ticket found!");
                    System.out.println("Response (1k): "
                            + response.substring(0,
                            Math.min(1024, response.length())));
                    break;
                default:
                    errorCount++;
                    System.out.println("Invalid response code " + post.getStatusCode() + " from CAS server!");
                    System.out.println("Response:" + response);
                    break;
            }
        } catch (final IOException e) {
            System.out.println(e.getMessage());
            errorCount++;
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    private static void notNull(Object object, String message) {
        if (object == null)
            throw new IllegalArgumentException(message);
    }

    public static void getServiceCall(String service, String serviceTicket) {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(service);

        NameValuePair simcard = new NameValuePair("ticket", serviceTicket);
        method.setQueryString(new NameValuePair[]{simcard});
        try {
            client.executeMethod(method);
            String response = method.getResponseBodyAsString();
            switch (method.getStatusCode()) {
                case 200:
                    System.out.println("Response html>>>: " + response);
                    break;
                default:
                    System.out.println("Invalid response code (" + method.getStatusCode()
                            + ") from CAS server!");
                    System.out.println("Response: " + response);
                    break;
            }
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        } finally {
            method.releaseConnection();
        }
    }

    public static void logout(String server, String ticketGrantingTicket) {
        HttpClient client = new HttpClient();
        DeleteMethod method = new DeleteMethod(server + "/" + ticketGrantingTicket);
        try {
            client.executeMethod(method);
            switch (method.getStatusCode()) {
                case 200:
                    System.out.println("Logged out");
                    break;
                default:
                    System.out.println("Invalid response code (" + method.getStatusCode()
                            + ") from CAS server!");
                    break;
            }
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        } finally {
            method.releaseConnection();
        }
    }

    public static String loginVerify(String username, String password) {
        CloseableHttpClient client = HttpClients.createDefault();
        String respContent = null;
        try {
            HttpPost post = new HttpPost("http://app-uat.gtcloud.cn/eoop-api/r/sys/rest/loginVerify");
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("username", username);
            jsonParam.put("password", password);
            jsonParam.put("deviceType", "1");
            jsonParam.put("device", "");
            StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            post.setEntity(entity);
            HttpResponse resp = client.execute(post);
            if (resp.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = resp.getEntity();
                respContent = EntityUtils.toString(he, "UTF-8");
            } else {
                errorCount++;
            }
            System.out.println(respContent);
            System.out.println("errorCount:" + errorCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respContent;
    }

    private static int errorCount = 0;

    public static String getTicketGrantingTicket1(String server, String username,
                                                  String password) {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(server);
        try {
            System.out.println("errorCount:" + errorCount);
            List<org.apache.http.NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse resp = client.execute(post);
            String response = EntityUtils.toString(resp.getEntity(), "UTF-8");
            int statusCode = resp.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 201:
                    Matcher matcher = Pattern.compile(".*action=\".*//*(.*?)\".*")
                            .matcher(response);
                    if (matcher.matches())
                        return server + "/" + matcher.group(1);
                    System.out.println("Successful ticket granting request, but no ticket found!");
                    System.out.println("Response (1k): "
                            + response.substring(0,
                            Math.min(1024, response.length())));
                    break;
                default:
                    errorCount++;
                    System.out.println("Invalid response code " + statusCode + " from CAS server!");
                    System.out.println("Response:" + response);
                    break;
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            errorCount++;
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    public static void main(String[] args) {
        /*String server = "http://172.19.50.212:8088/cas/mobile/tickets";
        String username = "458";
        String password = "1";
        String service = "http://172.19.50.212:8088/c/portal/login";

        String ticketGrantingTicket = getTicketGrantingTicket(server,
                username, password);

        System.out.println("TicketGrantingTicket is"+ticketGrantingTicket);

        String serviceTicket = getServiceTicket(server,
                ticketGrantingTicket, service);

        System.out.println("ServiceTicket is "+serviceTicket);
        getServiceCall(service, serviceTicket);
        logout(server, ticketGrantingTicket);*/

        System.out.println(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        for (int i = 0; i < 600; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        System.out.println(getTicketGrantingTicket1("http://app1-uat.gtcloud.cn/cas/remote/tickets", "dingyuan", "123456"));
                        System.out.println(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }).start();
        }

        //System.out.println(getTicketGrantingTicket1("http://app1-uat.gtcloud.cn/cas/remote/tickets", "dingyuan", "123456"));


       /* System.out.println(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        for (int i = 0; i < 600; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 50; i++) {
                        loginVerify("dingyuan", "3G9yhQkoBnrVZDdMeQ7GkQ==");
                        System.out.println(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }).start();
        }*/

    }
}
