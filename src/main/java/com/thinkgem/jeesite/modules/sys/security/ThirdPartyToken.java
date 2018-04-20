package com.thinkgem.jeesite.modules.sys.security;

import com.thinkgem.jeesite.modules.sys.entity.User;
import org.apache.shiro.authc.AuthenticationToken;

public class ThirdPartyToken extends org.apache.shiro.authc.UsernamePasswordToken implements AuthenticationToken {

    private static final long serialVersionUID = 8587329689973009598L;

    // the service ticket returned by the CAS server
    private String ticket = null;

    // the user identifier
    private String userId = null;

    // is the user in a remember me mode ?
    private boolean isRememberMe = false;

    public ThirdPartyToken(User user) {
        super(user.getLoginName(), user.getPassword());
        this.userId = user.getId();
    }

    public Object getPrincipal() {
        return userId;
    }

    public Object getCredentials() {
        return ticket;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isRememberMe() {
        return isRememberMe;
    }

    public void setRememberMe(boolean isRememberMe) {
        this.isRememberMe = isRememberMe;
    }

}
