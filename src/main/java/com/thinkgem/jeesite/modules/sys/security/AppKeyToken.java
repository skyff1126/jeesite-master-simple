package com.thinkgem.jeesite.modules.sys.security;

import com.thinkgem.jeesite.modules.sys.utils.APIUtil;

public class AppKeyToken extends org.apache.shiro.authc.UsernamePasswordToken {
    private String appName;
    private String appKey;

    public AppKeyToken(String appKey) {
        super(APIUtil.getUserName(appKey), APIUtil.getPassword(appKey));
        this.setRememberMe(false);
        this.appKey = appKey;
        this.appName = APIUtil.getAppName(appKey);
    }

    public AppKeyToken(String username, String password, String appName) {
        super(username, password);
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
