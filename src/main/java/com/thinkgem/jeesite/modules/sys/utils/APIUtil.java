package com.thinkgem.jeesite.modules.sys.utils;

import com.thinkgem.jeesite.modules.sys.exception.InvalidAppKeyException;
import com.thinkgem.jeesite.modules.sys.security.AppKeyToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class APIUtil {
    private static Logger logger = LoggerFactory.getLogger(APIUtil.class);

    public static String encodeAppKey(String appName, String account, String password) {
        String plainAppKey = appName + "|" + account + "|" + password;
        String appKey = Encodes.encodeHex(plainAppKey.getBytes());
        return appKey;
    }

    public static AppKeyToken normalize(String appKey) {
        AppKeyToken token = new AppKeyToken(APIUtil.getUserName(appKey),
                APIUtil.getPassword(appKey),
                APIUtil.getAppName(appKey));
        token.setAppKey(appKey);
        return token;
    }

    public static String[] decodeAppKey(String appKey) {
        String plainAppKey = new String(Encodes.decodeHex(appKey));
        String[] appKeyArray = plainAppKey.split("\\|");
        if (appKeyArray.length != 3) {
            logger.debug("invalidate app key string + " + Arrays.toString(appKeyArray));
            throw new InvalidAppKeyException("not validate format app key: " + plainAppKey);
        }
        return appKeyArray;
    }

    public static boolean isValidAppKey(String appKey) {
        boolean returnValue = false;
        try {
            String[] appKeyArray = decodeAppKey(appKey);
            if (appKeyArray.length == 3) {
                returnValue = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return returnValue;
    }

    public static boolean isValidAppKey(AppKeyToken token) {
        boolean returnValue = false;
        if (token != null) {
            if (token.getUsername() != null &&
                    token.getAppName() != null &&
                    token.getPassword() != null) {
                returnValue = true;
            }

        }
        return returnValue;
    }

    public static String getUserName(String appKey) {
        return decodeAppKey(appKey)[1];
    }

    public static String getPassword(String appKey) {
        return decodeAppKey(appKey)[2];
    }

    public static String getAppName(String appKey) {
        return decodeAppKey(appKey)[0];
    }
}
