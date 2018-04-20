package com.thinkgem.jeesite.modules.sys.utils;

import com.thinkgem.jeesite.common.init.ApplicationContext;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class MessageUtil {

    public static String getMessage(String key, Locale local, Object... parameters) {
        MessageSource message = (MessageSource) ApplicationContext.getContext().getBean("messageSource");
        if (local == null) {
            local = Locale.US;
        }
        return message.getMessage(key, parameters, local);
    }
}
