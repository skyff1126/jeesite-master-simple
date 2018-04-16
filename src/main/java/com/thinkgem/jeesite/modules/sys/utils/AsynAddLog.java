package com.thinkgem.jeesite.modules.sys.utils;

import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.modules.sys.entity.LoginLog;
import com.thinkgem.jeesite.modules.sys.service.LoginLogService;

public class AsynAddLog implements Runnable {

    private BaseService objectService;
    private Object entity;

    public AsynAddLog(BaseService objectService, Object entity) {
        this.objectService = objectService;
        this.entity = entity;
    }

    @Override
    public void run() {
        if (objectService instanceof UserOperationService) {
            ((UserOperationService) objectService).saveUserOperationLog((UserOperationLog) entity);
        } else if (objectService instanceof LoginLogService) {
            ((LoginLogService) objectService).saveLoginLog((LoginLog) entity);
        }
    }
}
