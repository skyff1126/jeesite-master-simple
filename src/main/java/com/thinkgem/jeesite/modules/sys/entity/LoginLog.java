package com.thinkgem.jeesite.modules.sys.entity;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;

public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String officeId;
    private String officeCode;
    private String companyId;
    private String name;
    private String loginName;
    private String gender;
    private String type; // LOGIN:登录 LOGOUT:登出
    private String clientType; // PC:PC端 MOBILE:手机端
    private String year;
    private String month;
    private String day;
    private String week;
    private String count;

    private String officeName;
    private String companyName;
    private String startTime;
    private String endTime;

    public LoginLog() {
        super();
        this.id = IdGen.uuid();
        this.year = DateUtils.getYear();
        this.month = DateUtils.getYearMonth();
        this.week = DateUtils.getWeek();
        this.day = DateUtils.getDay();
    }

    public LoginLog(String id) {
        this();
        this.id = id;
        this.year = DateUtils.getYear();
        this.month = DateUtils.getYearMonth();
        this.week = DateUtils.getWeek();
        this.day = DateUtils.getDay();
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    @ExcelField(title = "姓名", align = 2, sort = 40)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 1, max = 100)
    @ExcelField(title = "登录名", align = 2, sort = 30)
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

    @ExcelField(title = "性别", align = 2, sort = 50)
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    @Length(min = 1, max = 100)
    @ExcelField(title = "归属部门", align = 1, sort = 26)
    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    @ExcelField(title = "归属公司", align = 2, sort = 10)
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }
}
