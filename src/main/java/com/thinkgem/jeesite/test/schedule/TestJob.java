package com.thinkgem.jeesite.test.schedule;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component("TestJob")
@Lazy(false)
public class TestJob implements Serializable {

    private static final long serialVersionUID = -2073310586499744415L;

    private static final Logger logger = Logger.getLogger(TestJob.class);

    public void execute() {
        System.out.println("======定时任务测试1======" + new Date() + "======");
    }
}
