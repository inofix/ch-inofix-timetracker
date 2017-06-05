<%--
    add_button.jsp: add a task-record 
    
    Created:    2017-06-05 14:21 by Christian Berndt
    Modified:   2017-06-05 14:21 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp" %>

<%@page import="javax.portlet.WindowState"%>

<%
    // TODO: read window state behaviour from configuration
    String configuredWindowState =  WindowState.MAXIMIZED.toString();
    // configuredWindowState = LiferayWindowState.POP_UP.toString();
    
    // TODO: check permissions
%>

<liferay-frontend:add-menu>

    <portlet:renderURL var="editURL"
        windowState="<%= configuredWindowState %>">
        <portlet:param name="redirect" value="<%= currentURL %>" />
        <portlet:param name="mvcPath" value="/edit_task_record.jsp" />
        <portlet:param name="windowId" value="editTaskRecord" />
    </portlet:renderURL>

    <liferay-frontend:add-menu-item title='<%= LanguageUtil.get(request, "add-task-record") %>' url="<%= editURL.toString() %>" />
</liferay-frontend:add-menu>
