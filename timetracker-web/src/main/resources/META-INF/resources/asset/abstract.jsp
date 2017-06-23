<%--
    abstract.jsp: abstract task-record asset-renderer template

    Created:     2017-06-23 15:05 by Christian Berndt
    Modified:    2017-06-23 15:05 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp" %>

<%@page import="com.liferay.asset.kernel.model.AssetRenderer"%>

<%
    AssetRenderer<?> assetRenderer = (AssetRenderer<?>) request.getAttribute(WebKeys.ASSET_RENDERER);

    TaskRecord taskRecord = (TaskRecord) request.getAttribute(TimetrackerWebKeys.TASK_RECORD);

    Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletDisplay.getId());
    
    // TODO: add css config for full-content view
%>

<%-- 
<liferay-util:html-top outputKey="blogs_common_main_css">
    <link href="<%= PortalUtil.getStaticResourceURL(request, application.getContextPath() + "/blogs/css/common_main.css", portlet.getTimestamp()) %>" rel="stylesheet" type="text/css" />
</liferay-util:html-top>
--%>

<div class="portlet-timetracker">
    <div class="entry-body">
        <%= assetRenderer.getSummary(renderRequest, renderResponse) %>
    </div>
</div>
