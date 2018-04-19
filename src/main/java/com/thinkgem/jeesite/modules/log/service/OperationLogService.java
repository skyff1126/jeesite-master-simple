package com.thinkgem.jeesite.modules.log.service;

import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.modules.log.dao.OperationLogDao;
import com.thinkgem.jeesite.modules.log.entity.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OperationLogService extends BaseService {

    @Autowired
    private OperationLogDao operationLogDao;

    public List<OperationLog> queryOperationLogList(Map map) {
        return operationLogDao.queryOperationLogList(map);
    }

    public int listCount(Map map) {
        return operationLogDao.listCount(map);
    }

    @Transactional(readOnly = false)
    public void saveOperationLog(OperationLog operationLog) {
        operationLogDao.save(operationLog);
    }

}

