<%--
    display_styles_buttons.jsp: Select the display style of the Timetracker.
    
    Created:    2017-06-05 13:06 by Christian Berndt
    Modified:   2017-06-05 22:15 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp" %>

<%
    String navigation = ParamUtil.getString(request, "navigation", "all");

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
