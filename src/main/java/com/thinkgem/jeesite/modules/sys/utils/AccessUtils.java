package com.thinkgem.jeesite.modules.sys.utils;

public class AccessUtils {

    private static final ThreadLocal accessInfo = new ThreadLocal();

    public static AccessInfo getAccessInfo() {
        return (AccessInfo) accessInfo.get();
    }

    public static void setAccessInfo(AccessInfo info) {
        accessInfo.set(info);
    }
}
