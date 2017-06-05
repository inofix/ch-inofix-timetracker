<%--
    display_styles_buttons.jsp: Select the display style of the Timetracker.
    
    Created:    2017-06-05 13:06 by Christian Berndt
    Modified:   2017-06-05 13:06 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp" %>

<%
    String navigation = ParamUtil.getString(request, "navigation", "all");

    int total = GetterUtil.getInteger((String)request.getAttribute("view.jsp-total"));

    String displayStyle = ParamUtil.getString(request, "displayStyle");

    if (Validator.isNull(displayStyle)) {
        displayStyle = portalPreferences.getValue(PortletKeys.TIMETRACKER, "display-style", "list");
//         displayStyle = portalPreferences.getValue(PortletKeys.TIMETRACKER, "display-style", "descriptive");
    }
    
    PortletURL displayStyleURL = renderResponse.createRenderURL();
    // TODO: implement displayStyle descriptive
%>

<liferay-frontend:management-bar-display-buttons
    displayViews='<%= new String[] {"list"} %>'
    portletURL="<%= displayStyleURL %>"
    selectedDisplayStyle="<%= displayStyle %>"
/>