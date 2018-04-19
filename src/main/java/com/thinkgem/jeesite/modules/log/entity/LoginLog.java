package com.thinkgem.jeesite.modules.log.entity;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;

public class LoginLog extends BaseLog {

    private static final long serialVersionUID = 1L;

    private String type; // LOGIN:登录 LOGOUT:登出
    private String clientType; // PC:PC端 MOBILE:手机端
    private String year;
    private String month;
    private String day;
    private String week;
    private String count;

    public LoginLog() {
        super();
        this.year = DateUtils.getYear();
        this.month = DateUtils.getYearMonth();
        this.week = DateUtils.getWeek();
        this.day = DateUtils.getDay();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    @ExcelField(title = "登录次数", align = 2, sort = 60)
    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
