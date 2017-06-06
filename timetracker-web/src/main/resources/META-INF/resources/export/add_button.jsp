<%--
    add_button.jsp: add a task-record 
    
    Created:    2017-06-06 22:50 by Christian Berndt
    Modified:   2017-06-06 22:50 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp"%>

<%    
    // TODO: check permissions
%>

<liferay-frontend:add-menu>

    <portlet:renderURL var="addExportProcessURL">
        <portlet:param name="groupId" value="<%=String.valueOf(scopeGroupId)%>" />
        <portlet:param name="mvcPath" value="/export/new_export/export_task_records.jsp" />
        <portlet:param name="tabs1" value="export-import" />
        <portlet:param name="tabs2" value="export" />
    </portlet:renderURL>

    <liferay-frontend:add-menu-item title='<%= LanguageUtil.get(request, "new-export-process") %>' url="<%= addExportProcessURL.toString() %>" />
</liferay-frontend:add-menu>
