<%--
    add_button.jsp: create a new import process
    
    Created:    2017-06-08 00:32 by Christian Berndt
    Modified:   2017-11-10 15:36 by Christian Berndt
    Version:    1.0.4
--%>

<%@ include file="/init.jsp"%>

<%    
    boolean hasImportPermission = TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
            TimetrackerActionKeys.IMPORT_TASK_RECORDS);
%>

<liferay-frontend:add-menu>

    <portlet:renderURL var="addImportProcessURL">
        <portlet:param name="groupId" value="<%=String.valueOf(scopeGroupId)%>" />
        <portlet:param name="mvcPath" value="/import/new_import/import_task_records.jsp" />
        <portlet:param name="tabs1" value="export-import" />
    </portlet:renderURL>

    <c:if test="<%= hasImportPermission %>">
        <liferay-frontend:add-menu-item title='<%= LanguageUtil.get(request, "new-import-process") %>' url="<%= addImportProcessURL.toString() %>" />
    </c:if>
</liferay-frontend:add-menu>
