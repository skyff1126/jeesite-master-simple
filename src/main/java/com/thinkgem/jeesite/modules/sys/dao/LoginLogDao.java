package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.LoginLog;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface LoginLogDao {

    List<LoginLog> queryLoginLogList(Map map);

    int listCount(Map map);

    void save(LoginLog loginLog);

    List<LoginLog> queryLoginLogByMonth(Map map);

}
