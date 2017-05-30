<%--
    toolbar.jsp: the export toolbar.
    
    Created:    2017-05-16 17:30 by Christian Berndt
    Modified:   2017-05-16 17:30 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp" %>

<%@page import="com.liferay.portal.kernel.portlet.PortletURLUtil"%>

<%
    String mvcRenderCommandName = ParamUtil.getString(request, "mvcRenderCommandName");

    long groupId = ParamUtil.getLong(request, "groupId");
    boolean privateLayout = ParamUtil.getBoolean(request, "privateLayout");
    String displayStyle = ParamUtil.getString(request, "displayStyle", "descriptive");
    String orderByCol = ParamUtil.getString(request, "orderByCol");
    String orderByType = ParamUtil.getString(request, "orderByType");
    String navigation = ParamUtil.getString(request, "navigation", "all");
    String searchContainerId = ParamUtil.getString(request, "searchContainerId");

    // PortletURL portletURL = liferayPortletResponse.createRenderURL();

    portletURL.setParameter("mvcRenderCommandName", mvcRenderCommandName);
    portletURL.setParameter("groupId", String.valueOf(groupId));
    portletURL.setParameter("privateLayout", String.valueOf(privateLayout));
    portletURL.setParameter("displayStyle", displayStyle);
    portletURL.setParameter("navigation", navigation);
    portletURL.setParameter("orderByCol", orderByCol);
    portletURL.setParameter("orderByType", orderByType);
    portletURL.setParameter("searchContainerId", String.valueOf(searchContainerId));
%>

<% // TODO enable set operations %>
<liferay-frontend:management-bar
    includeCheckBox="<%= false %>"
    searchContainerId="<%= searchContainerId %>">
    <liferay-frontend:management-bar-filters>
    
        <liferay-frontend:management-bar-navigation
            navigationKeys='<%= new String[] {"all", "completed", "in-progress"} %>'
            navigationParam="navigation"
            portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
        />

        <liferay-frontend:management-bar-sort
            orderByCol="<%= orderByCol %>"
            orderByType="<%= orderByType %>"
            orderColumns='<%= new String[] {"create-date", "completion-date", "name"} %>'
            portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
        />
    </liferay-frontend:management-bar-filters>

    <liferay-frontend:management-bar-buttons>
        <liferay-frontend:management-bar-display-buttons
            displayViews='<%= new String[] {"descriptive", "list"} %>'
            portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
            selectedDisplayStyle="<%= displayStyle %>"
        />
    </liferay-frontend:management-bar-buttons>

    <liferay-frontend:management-bar-action-buttons>
        <liferay-frontend:management-bar-button href='<%= "javascript:" + liferayPortletResponse.getNamespace() + "deleteEntries();" %>' icon="times" label="delete" />
    </liferay-frontend:management-bar-action-buttons>
</liferay-frontend:management-bar>
