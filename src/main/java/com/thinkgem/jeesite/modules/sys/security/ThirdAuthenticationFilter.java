package com.thinkgem.jeesite.modules.sys.security;

import com.thinkgem.jeesite.modules.sys.dao.UserDao;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.EncryptUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Calendar;

@Service
public class ThirdAuthenticationFilter extends AuthenticatingFilter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // the name of the parameter service ticket in url
    private static final String UID = "uid";
    private static final String TIMESTAMP = "timestamp";
    private static final String ACCESSTOKEN = "token";
    private Calendar cal = Calendar.getInstance();

    @Value("${oa.sso.securityKey}")
    private String securityKey;

    // the url where the application is redirected if the CAS service ticket validation failed (example : /mycontextpatch/cas_error.jsp)
    private String failureUrl;

    @Autowired
    private UserDao userDao;

    /**
     * The token created for this authentication is a CasToken containing the CAS service ticket received on the CAS service url (on which
     * the filter must be configured).
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @throws Exception if there is an error processing the request.
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String accesstoken = httpRequest.getParameter(ACCESSTOKEN);
        String uid = httpRequest.getParameter(UID);
        String timestamp = httpRequest.getParameter(TIMESTAMP);

        BASE64Encoder base64Encoder = new BASE64Encoder();
        String token = base64Encoder.encode(EncryptUtil.MD5(uid + timestamp + securityKey).getBytes());

        long currentTimeStamp = System.currentTimeMillis();
        if(currentTimeStamp-Long.parseLong(timestamp) > 1000*60*30)
        {
            return new ThirdPartyToken(new User());
        }

        if(token.equals(accesstoken))
        {
            User user = userDao.findByLoginName(uid);
            if(null == user){
                return new ThirdPartyToken(new User());
            }
            return new ThirdPartyToken(user);
        }

        return new ThirdPartyToken(new User());
    }

    /**
     * Execute login by creating {@link #createToken(ServletRequest, ServletResponse) token} and logging subject
     * with this token.
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @throws Exception if there is an error processing the request.
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return executeLogin(request, response);
    }

    /**
     * Returns <code>false</code> to always force authentication (user is never considered authenticated by this filter).
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @param mappedValue the filter-specific config value mapped to this filter in the URL rules mappings.
     * @return <code>false</code>
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false;
    }

    /**
     * If login has been successful, redirect user to the original protected url.
     *
     * @param token the token representing the current authentication
     * @param subject the current authenticated subjet
     * @param request the incoming request
     * @param response the outgoing response
     * @throws Exception if there is an error processing the request.
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
        issueSuccessRedirect(request, response);
        return false;
    }

    /**
     * If login has failed, redirect user to the CAS error page (no ticket or ticket validation failed) except if the user is already
     * authenticated, in which case redirect to the default success url.
     *
     * @param token the token representing the current authentication
     * @param ae the current authentication exception
     * @param request the incoming request
     * @param response the outgoing response
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
                                     ServletResponse response) {
        // is user authenticated or in remember me mode ?
        Subject subject = getSubject(request, response);
        if (subject.isAuthenticated() || subject.isRemembered()) {
            try {
                issueSuccessRedirect(request, response);
            } catch (Exception e) {
                logger.error("Cannot redirect to the default success url", e);
            }
        } else {
            try {
                WebUtils.issueRedirect(request, response, failureUrl);
            } catch (IOException e) {
                logger.error("Cannot redirect to failure url : {}", failureUrl, e);
            }
        }
        return false;
    }

    public void setFailureUrl(String failureUrl) {
        this.failureUrl = failureUrl;
    }
}
