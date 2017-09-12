<%--
    navigation.jsp: Default navigation of Inofix' timetracker.
    
    Created:     2017-06-05 12:39 by Christian Berndt
    Modified:    2017-09-12 22:57 by Christian Berndt
    Version:     1.0.9
--%>

<%@ include file="/init.jsp" %>

<%
    PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setParameter("tabs1", "timetracker");
    
    PortletURL exportImportURL = renderResponse.createRenderURL();
    exportImportURL.setParameter("tabs1", "export-import"); 
    
    long formDateTime = ParamUtil.getLong(request, "formDate"); 
%>

<aui:nav-bar cssClass="collapse-basic-search" markupView="<%= markupView %>">

    <aui:nav cssClass="navbar-nav">
        <aui:nav-item href="<%= portletURL.toString() %>" label="timetracker" selected="<%= "timetracker".equals(tabs1) %>" />
        <aui:nav-item href="<%= exportImportURL.toString()  %>" label="export-import" selected="<%= "export-import".equals(tabs1) %>" />
    </aui:nav>

    <liferay-portlet:renderURL varImpl="searchURL">
        <liferay-portlet:param name="orderByCol" value="<%= ParamUtil.getString(request, "orderByCol") %>"/>
    </liferay-portlet:renderURL>

    <aui:form action="<%= searchURL.toString() %>" cssClass="task-record-search" name="searchFm">
         
        <c:if test="<%= formDateTime > 0 %>">           
            <div class="clear-message">
                <liferay-frontend:management-bar-button href='<%= portletURL.toString() %>' icon='times' label='clear' />      
                <aui:a cssClass="muted" href="<%= portletURL.toString() %>" label="clear-current-query-and-sorts"/>
            </div>
        </c:if> 
        
        <liferay-util:include page="/search_bar.jsp" servletContext="<%= application %>"/>      

    </aui:form>
    
</aui:nav-bar>

<aui:script>
    function <portlet:namespace />submitSearch() {
        
        var form = AUI.$(document.<portlet:namespace />searchFm);
        
        submitForm(form);
    }
</aui:script>
