<%--
    add_button.jsp: create a new import process
    
    Created:    2017-06-08 00:32 by Christian Berndt
    Modified:   2017-06-19 23:53 by Christian Berndt
    Version:    1.0.2
--%>

<%@ include file="/init.jsp"%>

<%    
    // TODO: check permissions
%>

<liferay-frontend:add-menu>

    <portlet:renderURL var="addImportProcessURL">
        <portlet:param name="groupId" value="<%=String.valueOf(scopeGroupId)%>" />
        <portlet:param name="mvcPath" value="/import/new_import/import_task_records.jsp" />
        <portlet:param name="tabs1" value="export-import" />
    </portlet:renderURL>

    <liferay-frontend:add-menu-item title='<%= LanguageUtil.get(request, "new-import-process") %>' url="<%= addImportProcessURL.toString() %>" />
</liferay-frontend:add-menu>
