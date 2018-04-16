package com.thinkgem.jeesite.common.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by Leon.wang on 2016/12/19.
 */
public class SimplePage<T> extends Page<T> {

    private Object otherInfo;

    public SimplePage() {
    }

    public SimplePage(int pageNo, int pageSize, long count, List<T> list) {
        setCount(count);
        setPageNo(pageNo);
        setPageSize(pageSize);
        setList(list);
    }

    public int getTotalPageNum() {
        return super.getTotalPage();
    }

    @Override
    @JsonIgnore
    public String getHtml() {
        return super.getHtml();
    }

    @Override
    @JsonIgnore
    public int getFirstResult() {
        return super.getFirstResult();
    }

    @Override
    @JsonIgnore
    public int getMaxResults() {
        return super.getMaxResults();
    }

    public Object getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(Object otherInfo) {
        this.otherInfo = otherInfo;
    }
}
