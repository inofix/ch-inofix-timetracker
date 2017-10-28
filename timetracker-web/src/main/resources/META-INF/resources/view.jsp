<%--
    view.jsp: Default view of Inofix' timetracker.
    
    Created:     2013-10-06 16:52 by Christian Berndt
    Modified:    2017-10-28 17:27 by Christian Berndt
    Version:     1.7.9
--%>

<%@ include file="/init.jsp" %>

<%
    String displayStyle = ParamUtil.getString(request, "displayStyle");
    
    String backURL = ParamUtil.getString(request, "backURL");
    String keywords = ParamUtil.getString(request, "keywords");
 
    PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setParameters(renderRequest.getParameterMap());
    portletURL.setParameter("redirect", ""); 
    portletURL.setParameter("tabs1", tabs1); 
    
    long ownerUserId = ParamUtil.getLong(request, "ownerUserId", -1); 

    int status = ParamUtil.getInteger(request, "status"); 
        
    TaskRecordSearch searchContainer = new TaskRecordSearch(renderRequest, "cur", portletURL);
    
    boolean reverse = false; 
    if (searchContainer.getOrderByType().equals("desc")) {
        reverse = true;
    }
    
    Sort sort = new Sort(searchContainer.getOrderByCol(), reverse);
    
    TaskRecordSearchTerms searchTerms = (TaskRecordSearchTerms) searchContainer.getSearchTerms();
    
    boolean ignoreFromDate = ParamUtil.getBoolean(request, "ignoreFromDate", true);

    Date fromDate = null;

    if (!ignoreFromDate) {

        int fromDateDay = ParamUtil.getInteger(request, "fromDateDay");
        int fromDateMonth = ParamUtil.getInteger(request, "fromDateMonth");
        int fromDateYear = ParamUtil.getInteger(request, "fromDateYear");
        fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay, fromDateYear);
    }
    
    boolean ignoreUntilDate = ParamUtil.getBoolean(request, "ignoreUntilDate", true);
    
    Date untilDate = null;

    if (!ignoreUntilDate) {

        int untilDateDay = ParamUtil.getInteger(request, "untilDateDay");
        int untilDateMonth = ParamUtil.getInteger(request, "untilDateMonth");
        int untilDateYear = ParamUtil.getInteger(request, "untilDateYear");
        untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay, untilDateYear);
    }
    
    Hits hits = null;

    if (searchTerms.isAdvancedSearch()) {

        hits = TaskRecordServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, ownerUserId,
                searchTerms.getWorkPackage(), searchTerms.getDescription(), status, fromDate, untilDate, null,
                searchTerms.isAndOperator(), searchTerms.isAdvancedSearch(), searchContainer.getStart(),
                searchContainer.getEnd(), sort);
        
    } else {

        hits = TaskRecordServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, 0, keywords,
                searchContainer.getStart(), searchContainer.getEnd(), sort);
    }

    List<TaskRecord> taskRecords = TaskRecordUtil.getTaskRecords(hits);

    searchContainer.setResults(taskRecords);
    searchContainer.setTotal(hits.getLength());

    request.setAttribute("view.jsp-columns", columns);

    request.setAttribute("view.jsp-displayStyle", displayStyle);

    request.setAttribute("view.jsp-searchContainer", searchContainer);

    request.setAttribute("view.jsp-total", hits.getLength());
%>

<% // TODO: add trash bin support %>

<liferay-util:include page="/navigation.jsp"
    servletContext="<%=application%>"/>

<c:choose>
    <c:when test="<%= "export-import".equals(tabs1) %>">
        <liferay-util:include page="/export_import.jsp" servletContext="<%= application %>"/>
    </c:when>
    <c:otherwise>
        <liferay-util:include page="/toolbar.jsp" servletContext="<%= application %>">
            <liferay-util:param name="searchContainerId" value="taskRecords" />
        </liferay-util:include>
        
        <div class="container-fluid-1280">
        
            <div id="<portlet:namespace />timetrackerContainer">
            
                <liferay-ui:error exception="<%= PrincipalException.class %>"
                    message="you-dont-have-the-required-permissions" />
                 
                <c:if test="<%= showSearchSpeed %>">  
                    <div class="alert alert-info">
                        <liferay-ui:search-speed hits="<%= hits %>" searchContainer="<%= searchContainer %>"/>
                    </div>
                </c:if> 
                                    
                <portlet:actionURL var="editSetURL"/>
                
                <aui:form action="<%= editSetURL %>" name="fm" 
                    onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "editSet();" %>'>
                    
                    <aui:input name="<%= Constants.CMD %>" type="hidden"/>  
                    <aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
                    <aui:input name="deleteTaskRecordIds" type="hidden" />
                            
                    <liferay-util:include page="/view_task_records.jsp" servletContext="<%= application %>" />
                
                </aui:form>
            </div>
        </div>
        
        <liferay-util:include page="/add_button.jsp" servletContext="<%= application %>" />    
    </c:otherwise>
</c:choose>

<%!
    private static Log _log = LogFactoryUtil.getLog("ch_inofix_timetracker_web.view_jsp");
%>
