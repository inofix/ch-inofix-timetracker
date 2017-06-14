<%--
    view.jsp: Default view of Inofix' timetracker.
    
    Created:     2013-10-06 16:52 by Christian Berndt
    Modified:    2017-06-14 23:03 by Christian Berndt
    Version:     1.7.0
--%>

<%@ include file="/init.jsp" %>

<%
    String [] columns = new String[] {"task-record-id", "work-package", "start-date"};
    String displayStyle = ParamUtil.getString(request, "displayStyle");
    
    int maxLength = 50; 
    
    if (Validator.isNotNull(timetrackerConfiguration)) {
        columns = portletPreferences.getValues("columns", timetrackerConfiguration.columns());
        maxLength = Integer.parseInt(portletPreferences.getValue("max-length", timetrackerConfiguration.maxLength()));
        //timeFormat = portletPreferences.getValue("time-format", timetrackerConfiguration.timeFormat());
    }
    
    String backURL = ParamUtil.getString(request, "backURL");
    String keywords = ParamUtil.getString(request, "keywords");
 
    PortletURL portletURL = renderResponse.createRenderURL();
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
    
    int fromDateDay = ParamUtil.getInteger(request, "fromDateDay"); 
    int fromDateMonth = ParamUtil.getInteger(request, "fromDateMonth"); 
    int fromDateYear = ParamUtil.getInteger(request, "fromDateYear"); 
    
    Date fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay, fromDateYear); 

    int untilDateDay = ParamUtil.getInteger(request, "untilDateDay"); 
    int untilDateMonth = ParamUtil.getInteger(request, "untilDateMonth"); 
    int untilDateYear = ParamUtil.getInteger(request, "untilDateYear"); 
    
    Date untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay, untilDateYear);
    
    
    Hits hits = null;

    if (searchTerms.isAdvancedSearch()) {
                
        hits = TaskRecordServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, ownerUserId,
                searchTerms.getWorkPackage(), searchTerms.getDescription(), status, fromDate,
                untilDate, null, searchTerms.isAndOperator(), searchContainer.getStart(),
                searchContainer.getEnd(), sort);
    } else {
        hits = TaskRecordServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, ownerUserId, keywords,
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
                    
                <portlet:actionURL name="editSet" var="editSetURL">
                </portlet:actionURL>          
                
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