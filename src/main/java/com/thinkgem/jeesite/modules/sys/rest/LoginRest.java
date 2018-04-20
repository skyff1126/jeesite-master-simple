package com.thinkgem.jeesite.modules.sys.rest;

import com.thinkgem.jeesite.modules.log.service.LoginLogService;
import com.thinkgem.jeesite.modules.sys.entity.Login;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.HResult;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${restPath}/sys/rest")
public class LoginRest {

    private static final Logger logger = Logger.getLogger("LoginRest");

    @RequestMapping(value = "/loginVerify", method = RequestMethod.POST)
    public HResult loginVerify(@RequestBody Login login) {
        HResult hResult = UserUtils.validatePassword(login.getPassword(), UserUtils.findUserByAccount(login.getAccount()));

        // add user login statistics data
        UserUtils.addLoginLog("LOGIN", "MOBILE");
        return hResult;
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
        HResult hResult = new HResult();
        try {
            // add user logout statistics data
            UserUtils.addLoginLog("LOGOUT", "MOBILE");
            hResult.setResult(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("auth:" + userId);
        }
        return hResult;
    }

}
