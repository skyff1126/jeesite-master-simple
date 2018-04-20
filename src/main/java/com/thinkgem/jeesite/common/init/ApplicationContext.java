package com.thinkgem.jeesite.common.init;

public class ApplicationContext {

    private static org.springframework.context.ApplicationContext appContext;

    public static org.springframework.context.ApplicationContext getContext() {
        return appContext;
    }

    public static void setContext(org.springframework.context.ApplicationContext context) {
        appContext = context;
    }
}
