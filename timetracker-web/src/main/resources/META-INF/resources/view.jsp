<%--
    view.jsp: Default view of Inofix' timetracker.
    
    Created:     2013-10-06 16:52 by Christian Berndt
    Modified:    2016-11-27 22:26 by Christian Berndt
    Version:     1.5.3
 --%>

<%@ include file="/init.jsp" %>

<%@page import="com.liferay.portal.kernel.search.Sort"%>

<%
    String backURL = ParamUtil.getString(request, "backURL");
    String keywords = ParamUtil.getString(request, "keywords");
    String tabs1 = ParamUtil.getString(request, "tabs1", "browse");
    
    // TODO: read from configuration
    int maxLength = 50; 
    boolean viewByDefault = false; 
    
    
    SearchContainer taskRecordSearch = new TaskRecordSearch(renderRequest, "cur", portletURL);
    
    boolean reverse = false; 
    if (taskRecordSearch.getOrderByType().equals("desc")) {
        reverse = true;
    }
    
    Sort sort = new Sort(taskRecordSearch.getOrderByCol(), reverse);
    
    TaskRecordSearchTerms searchTerms = (TaskRecordSearchTerms) taskRecordSearch.getSearchTerms();

    // TODO: use remote service
    Hits hits = TaskRecordLocalServiceUtil.search(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), keywords,
            taskRecordSearch.getStart(), taskRecordSearch.getEnd(), sort);
        
    List<Document> documents = ListUtil.toList(hits.getDocs());
        
    List<TaskRecord> taskRecords = new ArrayList<TaskRecord>();
    
    for (Document document : documents) {
        try {
            long taskRecordId = GetterUtil.getLong(document.get("entryClassPK"));
            // TODO: use remote service
            TaskRecord taskRecord = TaskRecordLocalServiceUtil.getTaskRecord(taskRecordId);
            taskRecords.add(taskRecord); 
        } catch (Exception e) {
            System.out.println(e); 
        }
    }

    taskRecordSearch.setResults(taskRecords); 
    taskRecordSearch.setTotal(hits.getLength());
%>

<div id="<portlet:namespace />timetrackerContainer">

    <liferay-ui:error exception="<%= PrincipalException.class %>"
        message="you-dont-have-the-required-permissions" />
        
        
    <liferay-ui:tabs
        names="browse,import-export"
        param="tabs1" url="<%= portletURL.toString() %>" />

    <c:choose>

        <c:when test='<%=tabs1.equals("import-export")%>'>
        
            <%
            // TODO: re-enable permission check
            %>
             <%--
            <c:if test='<%=TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
                                ActionKeys.ADD_TASK_RECORD)%>'>
            --%>
                <%@include file="/import.jspf"%>
                
            <%-- 
            </c:if>
            --%>
            
             <%--
            
             <%
            // TODO: re-enable permission check
            %>           
            <c:if
                test='<%=TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
                                ActionKeys.DELETE)%>'>
            --%>
                <%@include file="/delete_task_records.jspf"%>
                
            <%-- 
            </c:if>
            --%>
            
        </c:when>

        <c:otherwise>

            <%
                // TODO: enable toolbar
            %>  
            <%--             
            <liferay-ui:app-view-toolbar includeDisplayStyle="<%=true%>"
                includeSelectAll="<%=true%>">

                <liferay-util:include
                    servletContext="<%=session.getServletContext()%>"
                    page="/toolbar.jsp" />

            </liferay-ui:app-view-toolbar>
            --%>
            
            <portlet:actionURL name="editSet" var="editSetURL">
            </portlet:actionURL>
            
            
            <aui:form action="<%= editSetURL %>" name="fm" 
                onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "editSet();" %>'>
                
                <aui:input name="cmd" type="hidden" value="view"/>  
                
                <div id="<portlet:namespace />entriesContainer">
                
                    <div class="search-results">
                        <liferay-ui:search-speed hits="<%= hits %>" searchContainer="<%= taskRecordSearch %>" />
                    </div>
            
                    <liferay-ui:search-container
                        id="taskRecords"
                        searchContainer="<%= taskRecordSearch %>"
                        var="taskRecordearchContainer">
                        
                        <liferay-ui:search-container-row
                            className="ch.inofix.timetracker.model.TaskRecord"
                            modelVar="taskRecord" keyProperty="taskRecordId">
                            
                            <portlet:actionURL var="deleteURL" name="deleteTaskRecord">
                                <portlet:param name="taskRecordId"
                                    value="<%= String.valueOf(taskRecord.getTaskRecordId()) %>" />
                                <portlet:param name="backURL" value="<%= currentURL %>" />
                                <portlet:param name="mvcPath" value="/view.jsp" />
                            </portlet:actionURL>

                            <portlet:resourceURL var="downloadTaskRecordURL" id="serveTaskRecord">
                                <portlet:param name="taskRecordId"
                                    value="<%=String.valueOf(taskRecord.getTaskRecordId())%>" />
                            </portlet:resourceURL>
                            
                            <%-- 
                            <portlet:renderURL var="editURL">
                                --%>
                            <portlet:renderURL var="editURL"
                                windowState="<%= LiferayWindowState.POP_UP.toString() %>">
                                <portlet:param name="redirect" value="<%= currentURL %>" />
                                <portlet:param name="taskRecordId"
                                    value="<%= String.valueOf(taskRecord.getTaskRecordId()) %>" />
                                <portlet:param name="mvcPath" value="/edit_task_record.jsp" />
                                <portlet:param name="windowId" value="editTaskRecord" />
                            </portlet:renderURL>
        
                            <%
                                StringBuilder sb = new StringBuilder(); 
                            
                                sb.append(LanguageUtil.get(request, "permissions-of-task-record")); 
                                sb.append(" "); 
                                sb.append(taskRecord.getTaskRecordId()); 
                            
                                String modelResourceDescription = sb.toString(); 
                            %>
        
                            <liferay-security:permissionsURL
                                modelResource="<%= TaskRecord.class.getName() %>"
                                modelResourceDescription="<%= modelResourceDescription %>"
                                resourcePrimKey="<%= String.valueOf(taskRecord.getTaskRecordId()) %>"
                                var="permissionsURL" />
        
                            <portlet:actionURL var="viewURL" name="viewTaskRecord"
                                windowState="<%= LiferayWindowState.POP_UP.toString() %>">
                                <portlet:param name="redirect" value="<%= currentURL %>" />
                                <portlet:param name="taskRecordId"
                                    value="<%= String.valueOf(taskRecord.getTaskRecordId()) %>" />
                                <portlet:param name="mvcPath" value="/view_task_record.jsp" />
                                <portlet:param name="windowId" value="viewTaskRecord" />
                            </portlet:actionURL>
        
                            <%                  
                                String taglibEditURL = "javascript:Liferay.Util.openWindow({id: '" + renderResponse.getNamespace() + "editTaskRecord', title: '" + HtmlUtil.escapeJS(LanguageUtil.format(request, "edit-x", taskRecord.getTaskRecordId())) + "', uri:'" + HtmlUtil.escapeJS(editURL) + "'});";
                                String taglibViewURL = "javascript:Liferay.Util.openWindow({id: '" + renderResponse.getNamespace() + "viewTaskRecord', title: '" + HtmlUtil.escapeJS(LanguageUtil.format(request, "view-x", taskRecord.getTaskRecordId())) + "', uri:'" + HtmlUtil.escapeJS(viewURL) + "'});";

//                                 taglibEditURL = editURL;
                                        
                                boolean hasDeletePermission = TaskRecordPermission.contains(permissionChecker,
                                        taskRecord.getTaskRecordId(), ActionKeys.DELETE);   
                                boolean hasPermissionsPermission = TaskRecordPermission.contains(permissionChecker,
                                        taskRecord.getTaskRecordId(), ActionKeys.PERMISSIONS);  
                                boolean hasUpdatePermission = TaskRecordPermission.contains(permissionChecker,
                                        taskRecord.getTaskRecordId(), ActionKeys.UPDATE);
                                boolean hasViewPermission = TaskRecordPermission.contains(permissionChecker,
                                        taskRecord.getTaskRecordId(), ActionKeys.VIEW);
        
                                String detailURL = null;
        
                                if (hasUpdatePermission) {
                                    
                                    if (!viewByDefault) {
                                        detailURL = taglibEditURL; 
                                    } else {
                                        detailURL = taglibViewURL;                                  
                                    }
                                    
                                } else if (hasViewPermission) {
                                    detailURL = taglibViewURL;  
                                }
                            %>
        
                            <%@ include file="/search_columns.jspf"%>
                            <%-- 
                            --%>

                            <liferay-ui:search-container-column-text align="right">
        
                                <liferay-ui:icon-menu>
        
                                    <c:if test="<%= hasUpdatePermission %>">
                                        <liferay-ui:icon image="edit" url="<%=taglibEditURL%>" />
                                    </c:if>
                                    <c:if test="<%= hasPermissionsPermission %>">
                                        <liferay-ui:icon image="permissions" url="<%= permissionsURL %>" />
                                    </c:if>
                                    <c:if test="<%= hasViewPermission %>">
                                        <liferay-ui:icon image="view" url="<%=taglibViewURL%>" />
                                    </c:if> 
                                    <%-- <c:if test="<%= hasViewPermission %>">
                                        <liferay-ui:icon image="download" url="<%= downloadTaskRecordURL %>" />
                                    </c:if> --%>                                   
                                    <c:if test="<%= hasDeletePermission %>">
                                        <liferay-ui:icon-delete url="<%=deleteURL%>" />
                                    </c:if>
        
                                </liferay-ui:icon-menu>
        
                            </liferay-ui:search-container-column-text>                            
                        
                        </liferay-ui:search-container-row>
                        
                        <liferay-ui:search-iterator />                         
                
                    </liferay-ui:search-container>
                
                </div>
                
            </aui:form>

        </c:otherwise>
    </c:choose>
</div>
