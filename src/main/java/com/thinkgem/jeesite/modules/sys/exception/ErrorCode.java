package com.thinkgem.jeesite.modules.sys.exception;

import com.thinkgem.jeesite.modules.sys.utils.AccessUtils;
import com.thinkgem.jeesite.modules.sys.utils.LocaleUtils;
import com.thinkgem.jeesite.modules.sys.utils.MessageUtil;

import java.util.Locale;

public class ErrorCode {

    public static String ORG_OUT_OF_DELTA_RANGE = "org.outOfDeltaRange";
    public static String SYS_EXCEPTION = "sys.exception";
    public static String PAPI_AUTH_EXCEPTION = "papi.auth.exception";

    public static String parse(String errorCode, Object... parameters) {
        if (null == AccessUtils.getAccessInfo()) {
            return MessageUtil.getMessage(errorCode, null, parameters);
        }
        String languageCode = AccessUtils.getAccessInfo().getLanguageCode();
        Locale locale = null;
        if (languageCode != null) {
            locale = LocaleUtils.stringToLocale(languageCode);
        }

        return MessageUtil.getMessage(errorCode, locale, parameters);
    }

}
