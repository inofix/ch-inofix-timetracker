<%--
    add_button.jsp: create a new export process
    
    Created:    2017-06-06 22:50 by Christian Berndt
    Modified:   2017-11-19 13:56 by Christian Berndt
    Version:    1.0.3
--%>

<%@ include file="/init.jsp"%>

<%    
    boolean hasExportPermission = TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
            TimetrackerActionKeys.EXPORT_TASK_RECORDS);
%>

<liferay-frontend:add-menu>

    <portlet:renderURL var="addExportProcessURL">
        <portlet:param name="groupId" value="<%=String.valueOf(scopeGroupId)%>" />
        <portlet:param name="mvcRenderCommandName" value="exportTaskRecords" />
        <portlet:param name="redirect" value="<%= currentURL %>"/>
        <portlet:param name="tabs1" value="export-import" />
    </portlet:renderURL>
    
    <c:if test="<%= hasExportPermission %>">
        <liferay-frontend:add-menu-item  title='<%= LanguageUtil.get(request, "new-export-process") %>' url="<%= addExportProcessURL.toString() %>" />
    </c:if>
    
</liferay-frontend:add-menu>
