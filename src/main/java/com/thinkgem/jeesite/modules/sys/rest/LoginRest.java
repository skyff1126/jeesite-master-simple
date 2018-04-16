package com.thinkgem.jeesite.modules.sys.rest;

import com.thinkgem.jeesite.modules.sys.entity.Login;
import com.thinkgem.jeesite.modules.sys.entity.LoginLog;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.exception.AuthenException;
import com.thinkgem.jeesite.modules.sys.exception.PasswordResetException;
import com.thinkgem.jeesite.modules.sys.exception.UnknowAccountException;
import com.thinkgem.jeesite.modules.sys.service.LoginLogService;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.AsynAddLog;
import com.thinkgem.jeesite.modules.sys.utils.HResult;
import com.thinkgem.jeesite.modules.sys.utils.MyHttpClientUtils;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Controller
@RequestMapping(value = "${restPath}/sys/rest")
public class LoginRest {

    private static final Logger logger = Logger.getLogger("LoginRest");
    @Autowired
    private SystemService systemService;
    @Autowired
    private LoginLogService loginLogService;

    private static ExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);

    @RequestMapping(value = "/loginVerify", method = RequestMethod.POST)
    public String loginVerify(@RequestBody Login login){
        String email = null;
        String content = null;
        JSONObject obj = null;
        User user = null;
        try {
            email = login.getEmail_ID();
            String password = login.getUser_password();
            user = systemService.login(email, password);
        } catch (PasswordResetException e) {
            e.printStackTrace();
        } catch (AuthenException e) {
            e.printStackTrace();
        } catch (UnknowAccountException e) {
            e.printStackTrace();
        }

        // add user login statistics data
        LoginLog loginLog = new LoginLog();
        loginLog.setName(user.getName());
        loginLog.setLoginName(user.getLoginName());
        loginLog.setCompanyId(user.getCompany().getId());
        loginLog.setOfficeId(user.getOffice().getId());
        loginLog.setOfficeCode(user.getOffice().getCode());
        scheduledThreadPool.execute(new AsynAddLog(loginLogService, loginLog));
        
        return content;
    }

    /**
     * 登出接口
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public HResult logout(@RequestParam("userId") String userId) {
        logger.info("logout，userId=" + userId);
        HResult hr = new HResult();
        User user = systemService.getUser(userId);
        try {
            // add user logout statistics data
            LoginLog loginLog = new LoginLog();
            loginLog.setName(user.getName());
            loginLog.setLoginName(user.getLoginName());
            loginLog.setCompanyId(user.getCompany().getId());
            loginLog.setOfficeId(user.getOffice().getId());
            loginLog.setOfficeCode(user.getOffice().getCode());
            scheduledThreadPool.execute(new AsynAddLog(loginLogService, loginLog));
            hr.setResult(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("auth:" + userId);
        }
        return hr;
    }

}
