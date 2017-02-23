<%--
    error.jsp: error page of the timetracker portlet.

    Created:     2016-11-27 18:56 by Christian Berndt
    Modified:    2016-11-27 18:56 by Christian Berndt
    Version:     1.0.0
--%>


<%@ include file="/init.jsp" %>

<liferay-ui:error-header />

<liferay-ui:error exception="<%= NoSuchTaskRecordException.class %>" message="the-task-record-could-not-be-found" />

<liferay-ui:error-principal />