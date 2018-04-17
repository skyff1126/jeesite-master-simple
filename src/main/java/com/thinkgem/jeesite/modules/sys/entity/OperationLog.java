/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.thinkgem.jeesite.modules.sys.entity;

import com.thinkgem.jeesite.common.utils.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户Entity
 *
 * @author ThinkGem
 * @version 2013-5-15
 */
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String officeId;
    private String officeCode;
    private String companyId;
    private String userName;
    private String loginName; // 登录名
    private String menuName; //菜单名称
    private String moduleName; //业务模块
    private String operation; //操作描述

    private String startTime;
    private String endTime;

    public OperationLog() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        if (StringUtils.isENum(loginName)) {
            BigDecimal bd = new BigDecimal(loginName);
            return bd.toPlainString();
        } else {
            return loginName;
        }
    }

    public void setLoginName(String loginName) {
        if (StringUtils.isENum(loginName)) {
            BigDecimal bd = new BigDecimal(loginName);
            this.loginName = bd.toPlainString();
        } else {
            this.loginName = loginName;
        }
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
