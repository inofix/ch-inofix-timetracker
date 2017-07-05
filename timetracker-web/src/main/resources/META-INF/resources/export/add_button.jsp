<%--
    add_button.jsp: create a new export process
    
    Created:    2017-06-06 22:50 by Christian Berndt
    Modified:   2017-07-05 11:28 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp"%>

<%    
    boolean hasExportPermission = TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
            TaskRecordActionKeys.EXPORT_TASK_RECORDS);
%>

<liferay-frontend:add-menu>

    <portlet:renderURL var="addExportProcessURL">
        <portlet:param name="groupId" value="<%=String.valueOf(scopeGroupId)%>" />
        <portlet:param name="mvcPath" value="/export/new_export/export_task_records.jsp" />
        <portlet:param name="tabs1" value="export-import" />
    </portlet:renderURL>
    <c:if test="<%= hasExportPermission %>">
        <liferay-frontend:add-menu-item  title='<%= LanguageUtil.get(request, "new-export-process") %>' url="<%= addExportProcessURL.toString() %>" />
    </c:if>
</liferay-frontend:add-menu>
