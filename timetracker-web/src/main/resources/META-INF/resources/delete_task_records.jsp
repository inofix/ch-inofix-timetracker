<%--
    delete-task_records.jspf: Delete all task-records of this group
    
    Created:    2016-03-22 12:12 by Christian Berndt
    Modified:   2017-03-24 23:21 by Christian Berndt
    Version:    1.0.4
--%>

<%@ include file="/init.jsp" %>

<%
    boolean hasDeletePermission = TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
            TaskRecordActionKeys.DELETE_GROUP_TASK_RECORDS);
%>

<portlet:actionURL name="deleteGroupTaskRecords" var="deleteGroupTaskRecordsURL"> 
    <%-- We have to open the import-export tab        --%>
    <%-- since while deleting the records             --%>
    <%-- they are still found via the index, which    --%>
    <%-- results in npe-issues in the searchcontainer --%>
    <portlet:param name="tabs1" value="import-export"/>
</portlet:actionURL>

<aui:fieldset label="delete" cssClass="delete-section">
    <aui:button-row>
        <c:if test="<%= hasDeletePermission %>">
        <liferay-ui:icon-delete cssClass="btn btn-danger"       
            message="delete-group-task-records" label="true"
            url="<%=deleteGroupTaskRecordsURL.toString()%>" />
        </c:if>
        <c:if test="<%= !hasDeletePermission %>">
            <aui:button cssClass="btn btn-danger" disabled="<%= true %>" value="delete-group-task-records" />
        </c:if>
    </aui:button-row>
</aui:fieldset>
