package com.thinkgem.jeesite.modules.sys.rest;

import com.thinkgem.jeesite.modules.sys.entity.Login;
import com.thinkgem.jeesite.modules.log.entity.LoginLog;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.exception.AuthenException;
import com.thinkgem.jeesite.modules.sys.exception.PasswordResetException;
import com.thinkgem.jeesite.modules.sys.exception.UnknowAccountException;
import com.thinkgem.jeesite.modules.log.service.LoginLogService;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.AsynAddLog;
import com.thinkgem.jeesite.modules.sys.utils.HResult;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        UserUtils.addLoginLog("LOGIN", "MOBILE");
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
        try {
            // add user logout statistics data
            UserUtils.addLoginLog("LOGOUT", "MOBILE");
            hr.setResult(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("auth:" + userId);
        }
        return hr;
    }

}
