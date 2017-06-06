<%--
    navigation.jsp: Default navigation of Inofix' timetracker.
    
    Created:     2017-06-05 12:39 by Christian Berndt
    Modified:    2017-06-07 00:50 by Christian Berndt
    Version:     1.0.1
--%>

<%@ include file="/init.jsp" %>

<%
    PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setParameter("section", "timetracker");
    
    PortletURL exportImportURL = renderResponse.createRenderURL();
    exportImportURL.setParameter("section", "export-import"); 

    String section = ParamUtil.getString(request, "section", "timetracker");
%>

<aui:nav-bar cssClass="collapse-basic-search" markupView="<%= markupView %>">

    <aui:nav cssClass="navbar-nav">
        <aui:nav-item href="<%= portletURL.toString() %>" label="timetracker" selected="<%= "timetracker".equals(section) %>" />
        <aui:nav-item href="<%= exportImportURL.toString()  %>" label="export-import" selected="<%= "export-import".equals(section) %>" />
    </aui:nav>

    <aui:nav-bar-search>
        <liferay-portlet:renderURL varImpl="searchURL">
            <portlet:param name="redirect" value="<%= currentURL %>" />
        </liferay-portlet:renderURL>

        <aui:form action="<%= searchURL.toString() %>" name="searchFm">
            <liferay-ui:search-form
                page="/search.jsp"
                servletContext="<%= application %>"
            />
        </aui:form>
    </aui:nav-bar-search>
</aui:nav-bar>
