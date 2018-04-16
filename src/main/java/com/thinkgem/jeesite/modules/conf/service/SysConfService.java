package com.thinkgem.jeesite.modules.conf.service;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.conf.dao.SysConfDao;
import com.thinkgem.jeesite.modules.conf.entity.SysConf;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leon.wang on 2016/12/12.
 */
@Service
@Transactional(readOnly = true)
public class SysConfService extends CrudService<SysConfDao, SysConf> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public Map<String, String> loadConfigFromDB() {
        Map<String, String> configs = new HashMap<String, String>();
        List<SysConf> sysConfList = dao.findAllList(new SysConf());
        if (CollectionUtils.isNotEmpty(sysConfList)) {
            for (SysConf sysConf : sysConfList) {
                configs.put(sysConf.getName(), sysConf.getValue());
            }
        }
        return configs;
    }

    @Transactional(readOnly = false)
    @Override
    public void save(SysConf entity) {
        SysConf sysConf = dao.getByName(entity.getName());
        if (sysConf != null) {
            if (!sysConf.getValue().equals(entity.getValue())) {
                entity.setId(sysConf.getId());
                super.save(entity);
            }
        } else {
            super.save(entity);
        }
    }
}
