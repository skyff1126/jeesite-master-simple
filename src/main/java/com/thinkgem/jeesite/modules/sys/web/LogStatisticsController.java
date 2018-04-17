package com.thinkgem.jeesite.modules.sys.web;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.LoginLog;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.OperationLog;
import com.thinkgem.jeesite.modules.sys.service.LoginLogService;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.service.OperationLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
     *
     * @param loginLog
     * @param request
     * @param response
     * @param model
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = {"queryLoginLogList"})
    public String queryLoginLogList(LoginLog loginLog, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        HashMap queryMap = new HashMap();
        Page<LoginLog> page = new Page<>(request, response);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        if (null != loginLog) {
            if (!StringUtils.isEmpty(loginLog.getCompanyId())) {
                queryMap.put("companyId", loginLog.getCompanyId());
            }
            if (!StringUtils.isEmpty(loginLog.getOfficeId())) {
                Office temp = officeService.get(loginLog.getOfficeId());
                queryMap.put("officeCode", temp.getCode());
            }
            if (!StringUtils.isEmpty(loginLog.getName())) {
                queryMap.put("name", loginLog.getName());
            }
            if (!StringUtils.isEmpty(loginLog.getLoginName())) {
                queryMap.put("loginName", loginLog.getLoginName());
            }
            if (!StringUtils.isEmpty(loginLog.getStartTime())) {
                queryMap.put("startTime", df.parse(loginLog.getStartTime()));
            }
            if (!StringUtils.isEmpty(loginLog.getEndTime())) {
                Date date = df.parse(loginLog.getEndTime());
                queryMap.put("endTime", new Date(date.getTime() + 1000 * 60 * 60 * 24));
            }
        }

        queryMap.put("del", 0);

        int listCount = loginLogService.listCount(queryMap);
        if (listCount > 0) {
            page.setCount(listCount);
            page.initialize();
            int pageNo = page.getPageNo();
            int pageSize = page.getPageSize();
            int listStart = (pageNo - 1) * pageSize;
            queryMap.put("orderBy", page.getOrderBy());
            queryMap.put("listStart", listStart);
            queryMap.put("pageSize", pageSize);

            List<LoginLog> loginLogList = loginLogService.queryLoginLogList(queryMap);
            page.setList(loginLogList);
        }
        model.addAttribute("page", page);

        return "modules/log/loginLogList";
    }

    /**
     * 导出登录数据
     *
     * @param request
     * @param response
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "exportLoginLog", method = RequestMethod.POST)
    public String exportClientFile(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            HashMap queryMap = new HashMap();
            String fileName = "登录统计" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            String[] loginNames = request.getParameter("loginNames").split(",");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < loginNames.length; i++) {
                sb.append("'").append(loginNames[i]).append("',");
            }
            sb.deleteCharAt(sb.length() - 1);

            queryMap.put("loginNames", sb.toString());

            String[] officeIds = request.getParameter("officeIds").split(",");
            sb = new StringBuffer();
            for (int i = 0; i < officeIds.length; i++) {
                sb.append("'").append(officeIds[i]).append("',");
            }
            sb.deleteCharAt(sb.length() - 1);
            queryMap.put("officeIds", sb.toString());

            Page<LoginLog> page = new Page<LoginLog>(request, response);
            int listCount = loginLogService.listCount(queryMap);
            if (listCount > 0) {
                page.setCount(listCount);
                page.initialize();
                int pageNo = page.getPageNo();
                int pageSize = page.getPageSize();
                int listStart = (pageNo - 1) * pageSize;
                queryMap.put("orderBy", page.getOrderBy());
                queryMap.put("listStart", listStart);
                queryMap.put("pageSize", pageSize);

                List<LoginLog> loginLogList = loginLogService.queryLoginLogList(queryMap);
                page.setList(loginLogList);
            }

            new ExportExcel("登录统计", LoginLog.class).setDataList(page.getList()).write(response, fileName).dispose();

        } catch (Exception e) {
            addMessage(redirectAttributes, "导出数据失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/log/queryLoginLogList/?repage";
    }

    @RequestMapping(value = {"queryOperationLogList"})
    public String queryAdminOperationList(OperationLog operationLog, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        HashMap queryMap = new HashMap();
        Page<OperationLog> page = new Page<OperationLog>(request, response);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        if (null != operationLog) {
            if (!StringUtils.isEmpty(operationLog.getLoginName())) {
                queryMap.put("loginName", operationLog.getLoginName());
            }
            if (!StringUtils.isEmpty(operationLog.getStartTime())) {
                queryMap.put("startTime", df.parse(operationLog.getStartTime()));
            }
            if (!StringUtils.isEmpty(operationLog.getEndTime())) {
                Date date = df.parse(operationLog.getEndTime());
                queryMap.put("endTime", new Date(date.getTime() + 1000 * 60 * 60 * 24));
            }
        }

        queryMap.put("del", 0);

        int listCount = operationLogService.listCount(queryMap);
        if (listCount > 0) {
            page.setCount(listCount);
            page.initialize();
            int pageNo = page.getPageNo();
            int pageSize = page.getPageSize();
            int listStart = (pageNo - 1) * pageSize;
            queryMap.put("orderBy", page.getOrderBy());
            queryMap.put("listStart", listStart);
            queryMap.put("pageSize", pageSize);

            List<OperationLog> adminOperationServiceList = operationLogService.queryOperationLogList(queryMap);
            page.setList(adminOperationServiceList);
        }
        model.addAttribute("page", page);

        return "modules/sys/operationLogList";
    }
}

