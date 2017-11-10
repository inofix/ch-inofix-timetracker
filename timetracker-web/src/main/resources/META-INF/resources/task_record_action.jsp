<%--
    task_record_action.jsp: The action menu of the timetrackers's default view.
    
    Created:    2017-03-25 11:57 by Christian Berndt
    Modified:   2017-11-10 21:40 by Christian Berndt
    Version:    1.0.4
--%>

<%@ include file="/init.jsp"%>

<%
    ResultRow row = (ResultRow) request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

    TaskRecord taskRecord = (TaskRecord) row.getObject();
    
    String editURL = (String) request.getAttribute("editURL");
    String viewURL = (String) request.getAttribute("viewURL");
    
    editURL = HttpUtil.setParameter(editURL, renderResponse.getNamespace() + "taskRecordId", taskRecord.getTaskRecordId()); 
    viewURL = HttpUtil.setParameter(viewURL, renderResponse.getNamespace() + "taskRecordId", taskRecord.getTaskRecordId()); 

    boolean hasUpdatePermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
            TimetrackerActionKeys.UPDATE);
    boolean hasViewPermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
            TimetrackerActionKeys.VIEW);
    boolean hasDeletePermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
            TimetrackerActionKeys.DELETE);
    boolean hasPermissionsPermission = TaskRecordPermission.contains(permissionChecker, taskRecord, 
            TimetrackerActionKeys.PERMISSIONS);
%>

<liferay-ui:icon-menu showWhenSingleIcon="true">

    <c:if test="<%=hasViewPermission%>">

        <liferay-ui:icon iconCssClass="icon-eye-open" message="view" 
            url="<%= editURL %>" />

    </c:if>

    <c:if test="<%=hasUpdatePermission%>">

        <liferay-ui:icon iconCssClass="icon-edit" message="edit" 
            url="<%= viewURL %>" />

    </c:if>

    <c:if test="<%=hasDeletePermission%>">

        <portlet:actionURL name="editTaskRecord" var="deleteURL">
            <portlet:param name="cmd" value="<%= Constants.DELETE %>"/>
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
