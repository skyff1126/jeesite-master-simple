package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.OperationLog;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface OperationLogDao {

    List<OperationLog> queryOperationList(Map map);

    int listCount(Map map);

    void save(OperationLog operationLog);

}
