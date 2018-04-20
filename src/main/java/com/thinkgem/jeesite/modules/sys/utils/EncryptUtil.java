package com.thinkgem.jeesite.modules.sys.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.MessageDigest;

public class EncryptUtil {

    // MD5加码。32位
    public static String MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];


        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];


        byte[] md5Bytes = md5.digest(byteArray);


        StringBuffer hexValue = new StringBuffer();


        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String getMD5(String message) {
        String md5str = "";
        try {
            // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 2 将消息变成byte数组
            byte[] input = message.getBytes();

            // 3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);

            // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytesToHex(buff);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }

    public static void main(String[] args)
    {
       //单点登录地址
        String resquestUrl = "http://meeting.sunac.com.cn:81/mhome/login";

        //密钥双方保持一致，不能泄露给第三方，一旦泄露需更换，建议采用强密码的管理规则
        String securityKey = "Qazwsx4321";

        //IDM密码
        String uid = "15810217293";

        long timestamp = System.currentTimeMillis();
//        long timestamp = 1514169642423L;
        BASE64Encoder base64Encoder = new BASE64Encoder();

        // 校验和生成逻辑
        System.out.println("md5后的字符串：" + MD5(uid + timestamp + securityKey));
        String token = base64Encoder.encode(MD5(uid + timestamp + securityKey).getBytes());

        System.out.println(String.format("%s?uid=%s&timestamp=%d&token=%s", resquestUrl, uid, timestamp, token));

        BASE64Decoder base64Decoder = new BASE64Decoder();
        try {
            System.out.println("解码后的字符串：" + new String(base64Decoder.decodeBuffer(token)));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        java.util.Calendar cal = java.util.Calendar.getInstance();
//
//       System.out.println(java.util.Calendar.getInstance());
//        System.out.println(cal.get(java.util.Calendar.ZONE_OFFSET));

//        System.out.println(System.currentTimeMillis());

    }


}
