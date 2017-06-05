<%--
    view.jsp: Default view of Inofix' timetracker.
    
    Created:     2013-10-06 16:52 by Christian Berndt
    Modified:    2017-06-05 21:50 by Christian Berndt
    Version:     1.6.5
--%>

<%@ include file="/init.jsp" %>

<%@page import="com.liferay.portal.kernel.search.Sort"%>
<%@page import="com.liferay.portal.kernel.search.Field"%>

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
    portletURL.setParameter("tabs2", tabs2); 
    
//     SearchContainer searchContainer = new SearchContainer(liferayPortletRequest, null, null, "curEntry", SearchContainer.DEFAULT_DELTA, portletURL, null, "no-task-records-were-found");
    
    SearchContainer searchContainer = new TaskRecordSearch(renderRequest, "cur", portletURL);
    
    boolean reverse = false; 
    if (searchContainer.getOrderByType().equals("desc")) {
        reverse = true;
    }
    
    Sort sort = new Sort(searchContainer.getOrderByCol(), reverse);
    
    TaskRecordSearchTerms searchTerms = (TaskRecordSearchTerms) searchContainer.getSearchTerms();

    Hits hits = TaskRecordServiceUtil.search(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), keywords,
            searchContainer.getStart(), searchContainer.getEnd(), sort);
            
    List<Document> documents = ListUtil.toList(hits.getDocs());
        
    List<TaskRecord> taskRecords = new ArrayList<TaskRecord>();
    
    for (Document document : documents) {
        try {
            long taskRecordId = GetterUtil.getLong(document.get("entryClassPK"));

            TaskRecord taskRecord = TaskRecordServiceUtil.getTaskRecord(taskRecordId);
            taskRecords.add(taskRecord); 
        } catch (Exception e) {
            System.out.println("ERROR: timetracker/view.jsp Failed to getTaskRecord: " + e); 
        }
    }
    searchContainer.setResults(taskRecords); 
    searchContainer.setTotal(hits.getLength());
    
    request.setAttribute("view.jsp-columns", columns);
    
    request.setAttribute("view.jsp-displayStyle", displayStyle);
    
    request.setAttribute("view.jsp-searchContainer", searchContainer);
    
    request.setAttribute("view.jsp-total", hits.getLength());

%>

<% // TODO: add trash bin support %>

<liferay-util:include page="/navigation.jsp" servletContext="<%= application %>" />

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
                    
            <liferay-util:include page="/view_task_records.jsp" servletContext="<%= application %>" />
        
        </aui:form>
              
        <%--  
        <liferay-ui:tabs
            names="browse,export-import"
            param="tabs1" url="<%= portletURL.toString() %>" />
    
        <c:choose>
    
            <c:when test='<%=tabs1.equals("export-import")%>'>
            
     
            <liferay-ui:tabs
                names="export,import,delete"
                param="tabs2" url="<%= portletURL.toString() %>" /> 
                
                <c:if test="<%= tabs2.equals("export") %>">
                    <liferay-util:include page="/export/view.jsp" servletContext="<%= application %>"  >
                        <liferay-util:param name="orderByCol" value="create-date"/>
                    </liferay-util:include>
                </c:if>      
            
                <c:if test="<%= tabs2.equals("import") %>">
                    <liferay-util:include page="/import.jsp" servletContext="<%= application %>"  />
                </c:if>      
            
                <c:if test="<%= tabs2.equals("delete") %>">
                    <liferay-util:include page="/delete_task_records.jsp" servletContext="<%= application %>"  />
                </c:if>
                
            </c:when>
    
            <c:otherwise> 
    
                <liferay-util:include page="/view_task_records.jsp" servletContext="<%= application %>" />

            </c:otherwise>
        </c:choose>
        
        --%>
    </div>
</div>

<liferay-util:include page="/add_button.jsp" servletContext="<%= application %>" />
