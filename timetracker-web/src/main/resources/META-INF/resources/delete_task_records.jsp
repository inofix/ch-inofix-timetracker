<%--
    delete-task_records.jsp: Delete all task-records of this group
    
    Created:    2016-03-22 12:12 by Christian Berndt
    Modified:   2017-11-10 22:23 by Christian Berndt
    Version:    1.0.7
--%>

<%@ include file="/init.jsp" %>

<%
    boolean hasDeletePermission = TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
            TimetrackerActionKeys.DELETE_GROUP_TASK_RECORDS);
%>

<div class="container-fluid-1280">

    <portlet:actionURL var="deleteGroupRecordsURL">
        <portlet:param name="<%=Constants.CMD%>"
            value="deleteGroupTaskRecords" />
        <portlet:param name="groupId"
            value="<%=String.valueOf(scopeGroupId)%>" />
        <portlet:param name="mvcPath" value="/view.jsp" />
        <portlet:param name="tabs1" value="export-import" />
        <portlet:param name="tabs2" value="delete" />
    </portlet:actionURL>

    <aui:button-row>
        <c:if test="<%=hasDeletePermission%>">
            <liferay-ui:icon-menu>
                <liferay-ui:icon-delete cssClass="btn btn-danger"
                    message="delete-group-task-records"
                    url="<%=deleteGroupRecordsURL%>" />
            </liferay-ui:icon-menu>
        </c:if>
        <c:if test="<%=!hasDeletePermission%>">
            <aui:button cssClass="btn-danger" disabled="<%=true%>"
                value="delete-group-task-records" />
        </c:if>
    </aui:button-row>

    <div>Afterwards run "Reindex all search indexes" from the
        Server Configuration</div>
</div>
