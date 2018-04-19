package com.thinkgem.jeesite.modules.log.web;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.log.entity.BaseLog;
import com.thinkgem.jeesite.modules.log.entity.LoginLog;
import com.thinkgem.jeesite.modules.log.entity.OperationLog;
import com.thinkgem.jeesite.modules.log.service.LoginLogService;
import com.thinkgem.jeesite.modules.log.service.OperationLogService;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "${adminPath}/log")
public class LogStatisticsController extends BaseController {

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private OfficeService officeService;

    /**
     * 查询登录列表信息
     */
    @RequestMapping(value = {"queryLoginLogList"})
    public String queryLoginLogList(LoginLog loginLog, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        Page<LoginLog> page = new Page<>(request, response);
        HashMap queryMap = getQueryCondition(loginLog);

        int listCount = loginLogService.listCount(queryMap);
        if (listCount > 0) {
            setPageQueryCondition(page, queryMap, listCount);

            List<LoginLog> loginLogList = loginLogService.queryLoginLogList(queryMap);
            page.setList(loginLogList);
        }
        model.addAttribute("page", page);

        return "modules/log/loginLogList";
    }

    private HashMap getQueryCondition(BaseLog userLog) throws ParseException {
        HashMap queryMap = new HashMap();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (null != userLog) {
            if (!StringUtils.isEmpty(userLog.getCompanyId())) {
                queryMap.put("companyId", userLog.getCompanyId());
            }
            if (!StringUtils.isEmpty(userLog.getOfficeId())) {
                queryMap.put("officeId", userLog.getOfficeId());
            }
            if (!StringUtils.isEmpty(userLog.getUserName())) {
                queryMap.put("userName", userLog.getUserName());
            }
            if (!StringUtils.isEmpty(userLog.getLoginName())) {
                queryMap.put("loginName", userLog.getLoginName());
            }
            if (!StringUtils.isEmpty(userLog.getStartTime())) {
                queryMap.put("startTime", df.parse(userLog.getStartTime()));
            }
            if (!StringUtils.isEmpty(userLog.getEndTime())) {
                Date date = df.parse(userLog.getEndTime());
                queryMap.put("endTime", new Date(date.getTime() + 1000 * 60 * 60 * 24));
            }
        }
        return queryMap;
    }

    private void setPageQueryCondition(Page page, HashMap queryMap, int listCount) {
        page.setCount(listCount);
        page.initialize();
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();
        int listStart = (pageNo - 1) * pageSize;
        queryMap.put("orderBy", page.getOrderBy());
        queryMap.put("listStart", listStart);
        queryMap.put("pageSize", pageSize);
    }

    /**
     * 查询登录信息统计
     */
    @RequestMapping(value = {"queryLoginLogStatistics"})
    public String queryLoginLogStatistics(LoginLog loginLog, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        Page<LoginLog> page = new Page<>(request, response);
        HashMap queryMap = getQueryCondition(loginLog);

        int listCount = loginLogService.statisticsCount(queryMap);
        if (listCount > 0) {
            setPageQueryCondition(page, queryMap, listCount);
            List<LoginLog> loginLogList = loginLogService.queryLoginLogStatistics(queryMap);
            if (CollectionUtils.isNotEmpty(loginLogList)) {
                for (LoginLog log : loginLogList) {
                    Office office = officeService.get(log.getOfficeId());
                    if (office != null) {
                        log.setOfficeName(office.getName());
                    }
                    Office compnay = officeService.get(log.getCompanyId());
                    if (compnay != null) {
                        log.setCompanyName(compnay.getName());
                    }
                }
            }
            page.setList(loginLogList);
        }
        model.addAttribute("page", page);

        return "modules/log/loginLogLogStatistics";
    }

    /**
     * 导出登录数据
     */
    @RequestMapping(value = "exportLoginLogStatistics", method = RequestMethod.POST)
    public String exportLoginLogStatistics(LoginLog loginLog, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "登录统计" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            HashMap queryMap = getQueryCondition(loginLog);
            List<LoginLog> loginLogList = loginLogService.queryLoginLogList(queryMap);
            new ExportExcel("登录统计", LoginLog.class).setDataList(loginLogList).write(response, fileName).dispose();
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出数据失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/log/queryLoginLogList/?repage";
    }

    /**
     * 查询操作日志
     */
    @RequestMapping(value = {"queryOperationLogList"})
    public String queryOperationLogList(OperationLog operationLog, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        Page<OperationLog> page = new Page<>(request, response);
        HashMap queryMap = getQueryCondition(operationLog);

        int listCount = operationLogService.listCount(queryMap);
        if (listCount > 0) {
            setPageQueryCondition(page, queryMap, listCount);
            List<OperationLog> operationLogList = operationLogService.queryOperationLogList(queryMap);
            page.setList(operationLogList);
        }
        model.addAttribute("page", page);

        return "modules/log/operationLogList";
    }
}

