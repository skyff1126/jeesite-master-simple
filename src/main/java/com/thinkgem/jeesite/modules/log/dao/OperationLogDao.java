package com.thinkgem.jeesite.modules.log.dao;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.log.entity.OperationLog;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface OperationLogDao {

    List<OperationLog> queryOperationLogList(Map map);

    int listCount(Map map);

    void save(OperationLog operationLog);

}
