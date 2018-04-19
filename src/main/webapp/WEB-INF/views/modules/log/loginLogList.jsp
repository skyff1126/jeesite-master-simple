<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>客户端登录日志</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/dialog.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
            initDatepicker();
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").attr("action", "${ctx}/log/queryLoginLogList").submit();
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
        <li class="active"><a href="${ctx}/log/queryLoginLogList">客户端登录日志</a></li>
    </ul>
    <form:form id="searchForm" modelAttribute="loginLog" action="${ctx}/log/queryLoginLogList" method="post" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
        <div>
            <label>归属公司：</label><sys:treeselect id="company" name="companyId" value="${clientLoginData.companyId}"
                                                labelName="companyName" labelValue="${clientLoginData.companyName}"
                                                title="公司" url="/sys/office/treeData?type=1" cssClass="input-small"
                                                allowClear="true"/>
            <label>登录名：</label><form:input path="loginName" htmlEscape="false" maxlength="50" class="input-small"/>
            <label>开始时间：</label><form:input path="startTime" htmlEscape="false" maxlength="50" class="input-small"/>
            &nbsp;
            <label>结束时间：</label><form:input path="endTime" htmlEscape="false" maxlength="50" class="input-small"/>
            &nbsp;
        </div>
        <br/>
        <div>
            <label>归属部门：</label><sys:treeselect id="office" name="officeId" value="${clientLoginData.officeId}"
                                                labelName="officeName" labelValue="${clientLoginData.officeName}"
                                                title="部门" url="/sys/office/treeData?type=2" cssClass="input-small"
                                                allowClear="true"/>
            <label>姓&nbsp;&nbsp;&nbsp;名：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
            &nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>归属公司</th>
            <th>归属部门</th>
            <th>用户名</th>
            <th>登录名</th>
            <th>类型</th>
            <th>客户端类型</th>
            <th>时间</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="loginLog">
            <tr>
                <td>${loginLog.companyName}</td>
                <td>${loginLog.officeName}</td>
                <td>${loginLog.userName}</td>
                <td>${loginLog.loginName}</td>
                <td>${loginLog.type}</td>
                <td>${loginLog.clientType}</td>
                <td><fmt:formatDate value="${loginLog.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div class="pagination">${page}</div>
</body>
</html>