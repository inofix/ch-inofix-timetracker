<%--
    task_record_action.jsp: The action menu of the timetrackers's default view.
    
    Created:    2017-03-25 11:57 by Christian Berndt
    Modified:   2017-03-28 15:27 by Stefan Luebbers
    Version:    1.0.0
--%>

<%@ include file="/init.jsp"%>

<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>

<%
    ResultRow row = (ResultRow) request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

    TaskRecord taskRecord = (TaskRecord) row.getObject();
    
    String editURL = (String) request.getAttribute("editURL");
    String viewURL = (String) request.getAttribute("viewURL");
    
    editURL = HttpUtil.setParameter(editURL, renderResponse.getNamespace() + "taskRecordId", taskRecord.getTaskRecordId()); 
    viewURL = HttpUtil.setParameter(viewURL, renderResponse.getNamespace() + "taskRecordId", taskRecord.getTaskRecordId()); 
%>

<%
    String taglibEditURL = "javascript:Liferay.Util.openWindow({id: '" + renderResponse.getNamespace() + "editTaskRecord', title: '" + HtmlUtil.escapeJS(LanguageUtil.format(request, "edit-x", taskRecord.getTaskRecordId())) + "', uri:'" + HtmlUtil.escapeJS(editURL) + "'});";            
    String taglibViewURL = "javascript:Liferay.Util.openWindow({id: '" + renderResponse.getNamespace() + "viewTaskRecord', title: '" + HtmlUtil.escapeJS(LanguageUtil.format(request, "view-x", taskRecord.getTaskRecordId())) + "', uri:'" + HtmlUtil.escapeJS(viewURL) + "'});";
%>

<%
    boolean hasUpdatePermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
            TaskRecordActionKeys.UPDATE);
    boolean hasViewPermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
            TaskRecordActionKeys.VIEW);
    boolean hasDeletePermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
            TaskRecordActionKeys.DELETE);
    boolean hasPermissionsPermission = TaskRecordPermission.contains(permissionChecker, taskRecord, 
            TaskRecordActionKeys.PERMISSIONS);
%>

<liferay-ui:icon-menu showWhenSingleIcon="true">

    <c:if test="<%=hasViewPermission%>">

        <liferay-ui:icon iconCssClass="icon-eye-open" message="view" 
            url="<%=taglibViewURL%>" />

    </c:if>

    <c:if test="<%=hasUpdatePermission%>">

        <liferay-ui:icon iconCssClass="icon-edit" message="edit" 
            url="<%=taglibViewURL%>" />

    </c:if>

    <c:if test="<%=hasDeletePermission%>">

        <portlet:actionURL var="deleteURL" name="deleteTaskRecord">
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="taskRecordId"
                value="<%=String.valueOf(taskRecord.getTaskRecordId())%>" />
        </portlet:actionURL>

        <liferay-ui:icon-delete message="delete" url="<%=deleteURL%>" />

    </c:if>

    <c:if test="<%= hasPermissionsPermission %>">

        <liferay-security:permissionsURL
            modelResource="<%= TaskRecord.class.getName() %>"
            modelResourceDescription="<%= String.valueOf(taskRecord.getTaskRecordId()) %>"
            resourcePrimKey="<%= String.valueOf(taskRecord.getTaskRecordId()) %>"
            var="permissionsEntryURL"
            windowState="<%= LiferayWindowState.POP_UP.toString() %>" />

        <liferay-ui:icon iconCssClass="icon-cog" message="permissions"
            method="get" url="<%= permissionsEntryURL %>"
            useDialog="<%= true %>" />
    </c:if>

</liferay-ui:icon-menu>
