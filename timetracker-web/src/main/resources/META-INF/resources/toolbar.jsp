<%--
    toolbar.jsp: The toolbar of the timetracker portlet
    
    Created:    2016-03-20 16:58 by Christian Berndt
    Modified:   2017-06-10 16:31 by Christian Berndt
    Version:    1.1.8
--%>

<%@ include file="/init.jsp"%>

<%@page import="com.liferay.trash.kernel.util.TrashUtil"%>

<%
    String orderByCol = ParamUtil.getString(request, "orderByCol");

    String orderByType = ParamUtil.getString(request, "orderByType");

    String searchContainerId = ParamUtil.getString(request, "searchContainerId");
    
    int total = GetterUtil.getInteger(request.getAttribute("view.jsp-total"));
    
    PortletURL portletURL = renderResponse.createRenderURL();
%>

<liferay-frontend:management-bar
    disabled="<%= total == 0 %>"
    includeCheckBox="<%= true %>"
    searchContainerId="<%= searchContainerId %>"
>

    <liferay-frontend:management-bar-filters>
        <liferay-frontend:management-bar-sort
            orderByCol="<%= orderByCol %>"
            orderByType="<%= orderByType %>"
            orderColumns='<%= new String[] {"modified-date", "start-date", "task-record-id", "work-package"} %>'
            portletURL="<%= portletURL %>"
        />
    </liferay-frontend:management-bar-filters>
    
    <liferay-frontend:management-bar-buttons>            
        <liferay-util:include page="/display_style_buttons.jsp" servletContext="<%= application %>" />
    </liferay-frontend:management-bar-buttons>

    <liferay-frontend:management-bar-action-buttons>
        <%--    
        <liferay-frontend:management-bar-sidenav-toggler-button
            icon="info-circle"
            label="info"
        />
        --%>
        <liferay-frontend:management-bar-button href='<%= "javascript:" + renderResponse.getNamespace() + "deleteEntries();" %>' icon='<%= TrashUtil.isTrashEnabled(scopeGroupId) ? "trash" : "times" %>' label='<%= TrashUtil.isTrashEnabled(scopeGroupId) ? "recycle-bin" : "delete" %>' />
    </liferay-frontend:management-bar-action-buttons>
</liferay-frontend:management-bar>
