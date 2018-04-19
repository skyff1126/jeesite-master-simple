package com.thinkgem.jeesite.modules.log.dao;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.log.entity.LoginLog;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface LoginLogDao {

    List<LoginLog> queryLoginLogList(Map map);

    int listCount(Map map);

    List<LoginLog> queryLoginLogStatistics(Map map);

    int statisticsCount(Map map);

    void save(LoginLog loginLog);

}
