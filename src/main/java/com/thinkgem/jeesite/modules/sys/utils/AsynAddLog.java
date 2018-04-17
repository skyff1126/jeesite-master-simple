package com.thinkgem.jeesite.modules.sys.utils;

import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.modules.sys.entity.LoginLog;
import com.thinkgem.jeesite.modules.sys.entity.OperationLog;
import com.thinkgem.jeesite.modules.sys.service.LoginLogService;
import com.thinkgem.jeesite.modules.sys.service.OperationLogService;

public class AsynAddLog implements Runnable {

    private BaseService objectService;
    private Object entity;

    public AsynAddLog(BaseService objectService, Object entity) {
        this.objectService = objectService;
        this.entity = entity;
    }

    @Override
    public void run() {
        if (objectService instanceof OperationLogService) {
            ((OperationLogService) objectService).saveOperationLog((OperationLog) entity);
        } else if (objectService instanceof LoginLogService) {
            ((LoginLogService) objectService).saveLoginLog((LoginLog) entity);
        }
    }
}
