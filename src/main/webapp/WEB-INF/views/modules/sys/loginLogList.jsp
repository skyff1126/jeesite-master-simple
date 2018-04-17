<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>客户端登录统计</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/dialog.jsp" %>
    <style type="text/css">.sort {
        color: #0663A2;
        cursor: pointer;
    }</style>
    <script type="text/javascript">
        $(document).ready(function () {
            // 表格排序
            tableSort({callBack: page});

            initDatepicker();

            initCheckbox();

            $("#btnExportAll").click(function () {
                top.$.jBox.confirm("确认要导出统计数据吗？", "系统提示", function (v, h, f) {
                    if (v == "ok") {
                        $("#searchForm").attr("action", "${ctx}/log/exportAllClientLoginData").submit();
                    }
                }, {buttonsFocus: 1});
                top.$('.jbox-body .jbox-icon').css('top', '55px');
            });

            $("#btnExportSelected").click(function () {
                var ids = "";
                var officeIds = "";
                $(".singleCheck:checked").each(function (i, o) {
                    if (i == 0) {
                        ids += $(o).attr("name");
                        officeIds += $(o).val();
                    } else {
                        ids += "," + $(o).attr("name");
                        officeIds += "," + $(o).val();
                    }
                });
                if (ids == "") {
                    alert("请先选择需要导出的数据！")
                    return;
                }

                $('#loginNames').val(ids);
                $('#officeIds').val(officeIds);

                top.$.jBox.confirm("确认要导出统计数据吗？", "系统提示", function (v, h, f) {
                    if (v == "ok") {
                        $("#searchForm").attr("action", "${ctx}/log/exportLoginLog").submit();
                    }
                }, {buttonsFocus: 1});
                top.$('.jbox-body .jbox-icon').css('top', '55px');
            });
        });

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").attr("action", "${ctx}/log/queryLoginLogList").submit();
            return false;
        }

        function initCheckbox() {
            $(".multiCheck").click(function () {
                if ($(".multiCheck").attr("checked")) {
                    $(".singleCheck").attr("checked", true);
                } else {
                    $(".singleCheck").attr("checked", false);
                }
            });
            $(".singleCheck").click(function () {
                if ($(this).attr("checked") && $(".singleCheck").length == $(".singleCheck:checked").length) {
                    $(".multiCheck").attr("checked", true);
                }
                if (!$(this).attr("checked")) {
                    $(".multiCheck").attr("checked", false);
                }
            });
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
        <li class="active"><a href="${ctx}/log/queryLoginLogList">客户端登录统计</a></li>
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
            <input id="btnExportSelected" class="btn btn-primary" type="button" value="导出所选"/>
            <input id="btnExportAll" class="btn btn-primary" type="button" value="导出全部"/>
            <input id="loginNames" type="hidden" name="loginNames">
            <input id="officeIds" type="hidden" name="officeIds">
        </div>
    </form:form>

    <sys:message content="${message}"/>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th style="width:20px;"><input type="checkbox" class="multiCheck"></th>
            <th>归属公司</th>
            <th>归属部门</th>
            <th>登录名</th>
            <th>姓名</th>
            <th>性别</th>
            <th>登录次数</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="loginLog">
            <tr>
                <td>
                    <input type="checkbox" class="singleCheck" name="${loginLog.loginName}" value="${loginLog.officeId}">
                </td>
                <td>${loginLog.companyName}</td>
                <td>${loginLog.officeName}</td>
                <td>${loginLog.loginName}</td>
                <td>${loginLog.name}</td>
                <td>${loginLog.gender}</td>
                <td>${loginLog.count}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div class="pagination">${page}</div>
</body>
</html>