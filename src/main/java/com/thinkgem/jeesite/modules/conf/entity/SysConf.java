package com.thinkgem.jeesite.modules.conf.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * Created by Leon.wang on 2016/12/12.
 */
public class SysConf extends DataEntity<SysConf> {

    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
    private String description;

    public SysConf() {
        super();
    }

    public SysConf(String id) {
        super(id);
    }

    @Length(min = 1, max = 100, message = "name长度必须介于 1 和 100 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 1, max = 255, message = "value长度必须介于 1 和 255 之间")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Length(min = 1, max = 255, message = "description长度必须介于 1 和 255 之间")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}