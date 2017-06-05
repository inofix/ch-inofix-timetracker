<%--
    navigation.jsp: Default navigation of Inofix' timetracker.
    
    Created:     2017-06-05 12:39 by Christian Berndt
    Modified:    2017-06-05 12:39 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp" %>

<aui:nav-bar cssClass="collapse-basic-search" markupView="lexicon">
    <aui:nav cssClass="navbar-nav">
        <aui:nav-item label="timetracker" selected="<%= true %>" />
    </aui:nav>

    <aui:nav-bar-search>
        <liferay-portlet:renderURL varImpl="searchURL">
            <portlet:param name="redirect" value="<%= currentURL %>" />
        </liferay-portlet:renderURL>

        <aui:form action="<%= searchURL.toString() %>" name="searchFm">
            <liferay-ui:input-search markupView="lexicon" />
        </aui:form>
    </aui:nav-bar-search>
</aui:nav-bar>