<%--
    close_popup.jsp: Close the popup window 
    
    Created:    2016-03-21 17:12 by Christian Berndt
    Modified:   2017-03-22 17:10 by Christian Berndt
    Version:    1.0.1
--%>

<%@include file="/init.jsp" %>

<%@page import="com.liferay.portal.kernel.service.PortletLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.model.Portlet"%>

<%
    String redirect = request.getParameter("redirect");
    String windowId = ParamUtil.getString(request, "windowId", "editAsset");
    
    if (Validator.isNull(windowId)) {
        windowId = "editAsset"; 
    }

    redirect = PortalUtil.escapeRedirect(redirect);

    Portlet selPortlet = PortletLocalServiceUtil.getPortletById(
            company.getCompanyId(), portletDisplay.getId());
%>

<aui:script use="aui-base">
    Liferay.fire(
        'closeWindow',
        {
            id: '<portlet:namespace /><%= windowId %>',
            portletAjaxable: <%= selPortlet.isAjaxable() %>,

            <c:choose>
                <c:when test="<%= redirect != null %>">
                    redirect: '<%= HtmlUtil.escapeJS(redirect) %>'
                </c:when>
                <c:otherwise>
                    refresh: '<%= portletDisplay.getId() %>'
                </c:otherwise>
            </c:choose>
        }
    );
</aui:script>
