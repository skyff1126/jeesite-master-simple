package com.thinkgem.jeesite.modules.sys.utils;

import java.io.Serializable;

/*
 * The Result of Service API execute
 */
public class HResult implements Serializable {
    // RESULT:true , VALUE: Return Value
    // RESULT:false , VALUE: Error Message
    private boolean result = false;
    private String value;
    // if return value is an Object
    private Object objValue;

    public static HResult DEFAULT_OK = new HResult(true, "");

    public HResult(Boolean result, String value) {
        this.result = result;
        this.value = value == null ? "" : value;
    }

    public HResult(Boolean result, String value, Object objValue) {
        this.result = result;
        this.value = value == null ? "" : value;
        this.objValue = objValue;
    }

    public HResult(Object objValue) {
        this.result = true;
        this.value = "";
        this.objValue = objValue;
    }

    public boolean isOK() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getObjValue() {
        return objValue;
    }

    public void setObjValue(Object objValue) {
        this.objValue = objValue;
    }

    public HResult() {
    }
}
