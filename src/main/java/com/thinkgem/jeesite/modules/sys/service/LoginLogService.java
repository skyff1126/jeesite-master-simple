package com.thinkgem.jeesite.modules.sys.service;

import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.modules.sys.dao.LoginLogDao;
import com.thinkgem.jeesite.modules.sys.entity.LoginLog;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = false)
public class LoginLogService extends BaseService {

    @Autowired
    private LoginLogDao loginLogDao;

    @Autowired
    private SystemService systemService;

    public List<LoginLog> queryLoginLogList(Map map) {
        List<LoginLog> clientLoginDataList = loginLogDao.queryLoginLogList(map);
        transformLoginLogList(clientLoginDataList);
        return clientLoginDataList;
    }

    public int listCount(Map map) {
        return loginLogDao.listCount(map);
    }

    public void saveLoginLog(LoginLog clientLoginData) {
        loginLogDao.save(clientLoginData);
    }

    public List<LoginLog> queryLoginLogByMonth(Map map) {
        List<LoginLog> clientLoginDataList = loginLogDao.queryLoginLogByMonth(map);
        transformLoginLogList(clientLoginDataList);
        return clientLoginDataList;
    }

    private void transformLoginLogList(List<LoginLog> clientLoginDataList) {
        if (!clientLoginDataList.isEmpty()) {
            User user;
            for (LoginLog u : clientLoginDataList) {
                user = systemService.getUserByLoginName(u.getLoginName());
                if (null != user) {
                    u.setOfficeName(user.getOffice().getName());
                    u.setCompanyName(user.getCompany().getName());
                    u.setUserName(user.getName());
                }
            }
        }
    }
}

