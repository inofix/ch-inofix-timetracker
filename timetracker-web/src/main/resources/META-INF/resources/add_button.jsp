<%--
    add_button.jsp: add a task-record 
    
    Created:    2017-06-05 14:21 by Christian Berndt
    Modified:   2017-06-07 00.49 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp" %>

<%    
    // TODO: check permissions
%>

<liferay-frontend:add-menu>

    <portlet:renderURL var="addTaskRecordURL" >
        <portlet:param name="redirect" value="<%= currentURL %>" />
        <portlet:param name="mvcPath" value="/edit_task_record.jsp" />
        <portlet:param name="windowId" value="editTaskRecord" />
    </portlet:renderURL>

    <liferay-frontend:add-menu-item title='<%= LanguageUtil.get(request, "add-task-record") %>' url="<%= addTaskRecordURL.toString() %>" />
    
</liferay-frontend:add-menu>
