package com.thinkgem.jeesite.modules.log.entity;

public class OperationLog extends BaseLog {

    private static final long serialVersionUID = 1L;

    private String menuName; //菜单名称
    private String moduleName; //业务模块
    private String operation; //操作描述

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

}
