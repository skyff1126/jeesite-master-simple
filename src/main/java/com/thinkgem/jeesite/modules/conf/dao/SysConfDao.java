/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.conf.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.conf.entity.SysConf;

/**
 * Created by Leon.wang on 2016/12/12.
 */
@MyBatisDao
public interface SysConfDao extends CrudDao<SysConf> {

    public SysConf getByName(String name);
}