<%--
    full_content.jsp: full-content asset-renderer template

    Created:     2017-06-23 12:42 by Christian Berndt
    Modified:    2017-06-23 12:42 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp" %>

<%@page import="com.liferay.portal.kernel.service.PortletLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.model.Portlet"%>

<%
    TaskRecord taskRecord = (TaskRecord) request.getAttribute(TimetrackerWebKeys.TASK_RECORD);

    Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletDisplay.getId());
%>

<div class="portlet-timetracker">
    <div class="entry-body">
        <%
            String subtitle = null; // no subtitle
        %>

        <c:if test="<%= Validator.isNotNull(subtitle) %>">
            <div class="entry-subtitle">
                <p><%= HtmlUtil.escape(subtitle) %></p>
            </div>
        </c:if>

        <div class="entry-date icon-calendar">
            <span class="hide-accessible"><liferay-ui:message key="create-date" /></span>

            <%= dateFormatDateTime.format(taskRecord.getCreateDate()) %>
        </div>
        
        <div class="entry-date icon-calendar">
            <span class="hide-accessible"><liferay-ui:message key="modified-date" /></span>

            <%= dateFormatDateTime.format(taskRecord.getModifiedDate()) %>
        </div>

        <%= taskRecord.getDescription() %>  
        
        <div class="entry-date">
            <span class="hide-accessible"><liferay-ui:message key="from-until" /></span>

            <%= dateFormatDateTime.format(taskRecord.getFromDate()) %> - <%= dateFormatDateTime.format(taskRecord.getUntilDate()) %>
            
        </div>  
        
        <div>
            <liferay-ui:message key="duration" />: <%= taskRecord.getDurationInHours() %>
        </div>
    </div>
</div>

