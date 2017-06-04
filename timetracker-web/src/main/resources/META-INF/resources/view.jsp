<%--
    view.jsp: Default view of Inofix' timetracker.
    
    Created:     2013-10-06 16:52 by Christian Berndt
    Modified:    2017-06-04 23:32 by Christian Berndt
    Version:     1.6.3
 --%>

<%@ include file="/init.jsp" %>

<%@page import="com.liferay.portal.kernel.search.Sort"%>
<%@page import="com.liferay.portal.kernel.search.Field"%>

<%
    String [] columns = new String[] {"task-record-id", "work-package", "start-date"};
    int maxLength = 50; 
    boolean viewByDefault = false;
    
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
    
    TaskRecordSearch taskRecordSearch = new TaskRecordSearch(renderRequest, "cur", portletURL);
    
    boolean reverse = false; 
    if (taskRecordSearch.getOrderByType().equals("desc")) {
        reverse = true;
    }
    
    Sort sort = new Sort(taskRecordSearch.getOrderByCol(), reverse);
    
    TaskRecordSearchTerms searchTerms = (TaskRecordSearchTerms) taskRecordSearch.getSearchTerms();

    Hits hits = TaskRecordServiceUtil.search(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), keywords,
            taskRecordSearch.getStart(), taskRecordSearch.getEnd(), sort);
            
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
    taskRecordSearch.setResults(taskRecords); 
    taskRecordSearch.setTotal(hits.getLength());
%>

<div id="<portlet:namespace />timetrackerContainer">

    <liferay-ui:error exception="<%= PrincipalException.class %>"
        message="you-dont-have-the-required-permissions" />        
        
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

            <liferay-util:include page="/toolbar.jsp" servletContext="<%= application %>" />
            
            <portlet:actionURL name="editSet" var="editSetURL">
            </portlet:actionURL>          
            
            <aui:form action="<%= editSetURL %>" name="fm" 
                onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "editSet();" %>'>
                
                <aui:input name="cmd" type="hidden" value="view"/>  
                
                <div id="<portlet:namespace />entriesContainer">
                
                    <% 
                        // TODO: make search speed display configurable 
                    %>
                    <%--
                    <div class="search-results">
                        <liferay-ui:search-speed hits="<%= hits %>" searchContainer="<%= taskRecordSearch %>" />
                    </div>
                    --%>
            
                    <liferay-ui:search-container
                        id="taskRecords"
                        searchContainer="<%= taskRecordSearch %>"
                        var="taskRecordearchContainer">

                        <liferay-ui:search-container-row
                            className="ch.inofix.timetracker.model.TaskRecord"
                            modelVar="taskRecord"
                            keyProperty="taskRecordId">

                            <portlet:renderURL var="editURL"
                                windowState="<%=LiferayWindowState.POP_UP.toString()%>">
                                <portlet:param name="redirect"
                                    value="<%=currentURL%>" />
                                <portlet:param name="taskRecordId"
                                    value="<%=String.valueOf(taskRecord.getTaskRecordId())%>" />
                                <portlet:param name="mvcPath"
                                    value="/edit_task_record.jsp" />
                                <portlet:param name="windowId"
                                    value="editTaskRecord" />
                            </portlet:renderURL>

                            <portlet:renderURL var="viewURL"
                                windowState="<%=LiferayWindowState.POP_UP.toString()%>">
                                <portlet:param name="redirect"
                                    value="<%=currentURL%>" />
                                <portlet:param name="taskRecordId"
                                    value="<%=String.valueOf(taskRecord.getTaskRecordId())%>" />
                                <portlet:param name="mvcPath"
                                    value="/edit_task_record.jsp" />
                                <portlet:param name="windowId"
                                    value="viewTaskRecord" />
                            </portlet:renderURL>

                            <%
                                String taglibEditURL = "javascript:Liferay.Util.openWindow({id: '" + renderResponse.getNamespace() + "editTaskRecord', title: '" + HtmlUtil.escapeJS(LanguageUtil.format(request, "edit-x", taskRecord.getTaskRecordId())) + "', uri:'" + HtmlUtil.escapeJS(editURL) + "'});";            
                                String taglibViewURL = "javascript:Liferay.Util.openWindow({id: '" + renderResponse.getNamespace() + "viewTaskRecord', title: '" + HtmlUtil.escapeJS(LanguageUtil.format(request, "view-x", taskRecord.getTaskRecordId())) + "', uri:'" + HtmlUtil.escapeJS(viewURL) + "'});";
                                
                                request.setAttribute("editURL", editURL.toString()); 
                                request.setAttribute("viewURL", viewURL.toString()); 
                              
                                boolean hasUpdatePermission = TaskRecordPermission.contains(permissionChecker,
                                        taskRecord.getTaskRecordId(), TaskRecordActionKeys.UPDATE);
                                boolean hasViewPermission = TaskRecordPermission.contains(permissionChecker,
                                        taskRecord.getTaskRecordId(), TaskRecordActionKeys.VIEW);

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


                            <liferay-ui:search-container-column-jsp
                                cssClass="entry-action"
                                path="/task_record_action.jsp"
                                valign="top" />

                        </liferay-ui:search-container-row>

                        <liferay-ui:search-iterator />
                
                    </liferay-ui:search-container>
                    
                    <liferay-ui:message key="sum"/> = <strong><span id="sum"></span></strong>                                     
                
                </div>
                
            </aui:form>


            <%
                ResourceURL resourceURL = liferayPortletResponse.createResourceURL();

                resourceURL.setResourceID("getSum");

                // Copy render parameters to resourceRequest
                resourceURL.setParameters(renderRequest.getParameterMap());
            %>

            <aui:script use="aui-io-request">
        
                AUI().ready('aui-io-request',
                    function (A) {
                        A.io.request(
                            '<%= resourceURL.toString() %>',
                            {
                                on: {
                                    success: function() {
                                        var data = this.get('responseData');
                                        A.one('#sum').setHTML(data); 
                                    }
                                }
                            }
                        );
                     }
                 );
            </aui:script>
        </c:otherwise>
    </c:choose>
</div>
