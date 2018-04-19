<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>操作日志</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/dialog.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
            initDatepicker();
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").attr("action", "${ctx}/log/queryOperationLogList").submit();
            return false;
        }
        function initDatepicker() {
            var param = {
                format: 'yyyy-mm-dd',
                weekStart: 1,
                autoclose: true,
                todayBtn: 'linked',
                language: 'zh-CN'
            };
            $("#startTime").datepicker(param);
            $("#endTime").datepicker(param);
        }
    </script>
</head>
<body>
    <ul class="nav nav-tabs">
        <li class="active"><a href="${ctx}/log/queryOperationLogList">操作日志列表</a></li>
    </ul>
    <form:form id="searchForm" modelAttribute="operationLog" action="${ctx}/log/queryOperationLogList" method="post"
               class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
        <div>
            <label>登录名：</label><form:input path="loginName" htmlEscape="false" maxlength="50" class="input-small"/>
            <label>开始时间：</label><form:input path="startTime" htmlEscape="false" maxlength="50" class="input-small"/>
            &nbsp;
            <label>结束时间：</label><form:input path="endTime" htmlEscape="false" maxlength="50" class="input-small"/>
            &nbsp;
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
        </div>
    </form:form>
    <tags:message content="${message}"/>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>归属公司</th>
            <th>归属部门</th>
            <th>用户名</th>
            <th>登录名</th>
            <th>菜单名称</th>
            <th>业务模块</th>
            <th>操作</th>
            <th>时间</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="operationLog">
            <tr>
                <td>${operationLog.companyName}</td>
                <td>${operationLog.officeName}</td>
                <td>${operationLog.userName}</td>
                <td>${operationLog.loginName}</td>
                <td>${operationLog.menuName}</td>
                <td>${operationLog.moduleName}</td>
                <td>${operationLog.operation}</td>
                <td>
                    <fmt:formatDate value="${operationLog.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div class="pagination">${page}</div>
</body>
</html>