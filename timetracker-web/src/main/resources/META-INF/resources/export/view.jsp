<%--
    export.jsp: Export taskRecords in background to a file.
    
    Created:    2017-04-18 23:11 by Christian Berndt
    Modified:   2017-06-01 18:56 by Christian Berndt
    Version:    1.0.3
--%>

<%@ include file="/init.jsp"%>

<%@page import="ch.inofix.timetracker.background.task.TaskRecordExportBackgroundTaskExecutor"%>

<%@page import="com.liferay.background.task.kernel.util.comparator.BackgroundTaskComparatorFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTask"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil"%>
<%@page import="com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker"%>
<%@page import="com.liferay.portal.kernel.dao.search.RowChecker"%>
<%@page import="com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil"%>
<%@page import="com.liferay.portal.kernel.repository.model.FileEntry"%>
<%@page import="com.liferay.portal.kernel.util.TextFormatter"%>
<%@page import="com.liferay.portal.kernel.util.OrderByComparator"%>
<%@page import="com.liferay.portal.kernel.util.StringBundler"%>

<portlet:renderURL var="exportTaskRecordsURL">
    <portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>"/>
    <portlet:param name="mvcPath" value="/export/new_export/export_task_records.jsp"/>
    <portlet:param name="tabs1" value="export-import" />
    <portlet:param name="tabs2" value="export" />
</portlet:renderURL>

<aui:button-row>
    <aui:button href="<%= exportTaskRecordsURL %>" value="new-export"/>
</aui:button-row>

<%
    String displayStyle = ParamUtil.getString(request, "displayStyle", "list"); 
    long groupId = scopeGroupId; 
    String navigation = ParamUtil.getString(request, "navigation", "all"); 
    String orderByCol = ParamUtil.getString(request, "orderByCol", "create-date");
    String orderByType = ParamUtil.getString(request, "orderByType", "desc");
    String searchContainerId = "SearchContainer"; 

    portletURL.setParameter("groupId", String.valueOf(groupId));
    portletURL.setParameter("displayStyle", displayStyle);
    portletURL.setParameter("navigation", navigation);
    portletURL.setParameter("orderByCol", orderByCol);
    portletURL.setParameter("orderByType", orderByType);
    portletURL.setParameter("searchContainerId", searchContainerId);

    OrderByComparator<BackgroundTask> orderByComparator = BackgroundTaskComparatorFactoryUtil
            .getBackgroundTaskOrderByComparator(orderByCol, orderByType);
%>

<div id="<portlet:namespace />exportProcessesSearchContainer">
    <%
        int incompleteBackgroundTaskCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(scopeGroupId, TaskRecordExportBackgroundTaskExecutor.class.getName(), false);
    %>
    <div class="<%= (incompleteBackgroundTaskCount == 0) ? "hide" : "in-progress" %>" id="<portlet:namespace />incompleteProcessMessage">
        <liferay-util:include page="/incomplete_processes_message.jsp" servletContext="<%= application %>">
            <liferay-util:param name="incompleteBackgroundTaskCount" value="<%= String.valueOf(incompleteBackgroundTaskCount) %>" />
        </liferay-util:include>
    </div>

    <liferay-util:include page="/export/toolbar.jsp" servletContext="<%= application %>">
        <liferay-util:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>" />
        <liferay-util:param name="displayStyle" value="<%= displayStyle %>" />
        <liferay-util:param name="navigation" value="<%= navigation %>" />
        <liferay-util:param name="orderByCol" value="<%= orderByCol %>" />
        <liferay-util:param name="orderByType" value="<%= orderByType %>" />
        <liferay-util:param name="searchContainerId" value="<%= searchContainerId %>" />
    </liferay-util:include>

    <portlet:actionURL name="deleteBackgroundTasks" var="deleteBackgroundTasksURL">
        <portlet:param name="redirect" value="<%= currentURL.toString() %>" />
    </portlet:actionURL>
    
    <aui:form action="<%= deleteBackgroundTasksURL %>" method="get" name="fm">
        <aui:input name="<%= Constants.CMD %>" type="hidden" />
        <aui:input name="redirect" type="hidden" value="<%= currentURL.toString() %>" />
        <aui:input name="deleteBackgroundTaskIds" type="hidden" />
    
        <%
            RowChecker rowChecker =  new EmptyOnClickRowChecker(liferayPortletResponse);
        %>
        
        <liferay-ui:search-container
            emptyResultsMessage="no-export-processes-were-found"
            id="<%= searchContainerId %>"
            iteratorURL="<%= portletURL %>"
            orderByCol="<%= orderByCol %>"
            orderByComparator="<%= orderByComparator %>"
            orderByType="<%= orderByType %>"
            rowChecker="<%= rowChecker %>">
            
            <liferay-ui:search-container-results>
            
            <%
                int backgroundTasksCount = 0;
                List<BackgroundTask> backgroundTasks = null;
                
                boolean completed = false;
                
                if ("completed".equals(navigation)) {
                    completed = true; 
                } 

                backgroundTasks = BackgroundTaskManagerUtil.getBackgroundTasks(scopeGroupId, TaskRecordExportBackgroundTaskExecutor.class.getName(), 0, 20, orderByComparator);
                backgroundTasksCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(scopeGroupId, TaskRecordExportBackgroundTaskExecutor.class.getName());

                searchContainer.setResults(backgroundTasks);
                searchContainer.setTotal(backgroundTasksCount);
            %>
    
            </liferay-ui:search-container-results>
    
            <liferay-ui:search-container-row
                className="com.liferay.portal.kernel.backgroundtask.BackgroundTask"
                keyProperty="backgroundTaskId" modelVar="backgroundTask">
    
                <liferay-ui:search-container-column-text>
                    <liferay-ui:user-portrait cssClass="user-icon-lg"
                        userId="<%=backgroundTask.getUserId()%>" />
                </liferay-ui:search-container-column-text>
    
                <liferay-ui:search-container-column-text
                    cssClass="table-cell-content" name="title">
                    <span id="<%=liferayPortletResponse.getNamespace() + "backgroundTaskName"
                                        + String.valueOf(backgroundTask.getBackgroundTaskId())%>">
                        <liferay-ui:message
                            key="<%=HtmlUtil.escape(backgroundTask.getName())%>" />
                    </span>
                </liferay-ui:search-container-column-text>
                
                <liferay-ui:search-container-column-date
                    name="create-date"
                    orderable="<%= true %>"
                    value="<%= backgroundTask.getCreateDate() %>"
                />
    
                <liferay-ui:search-container-column-date
                    name="completion-date"
                    orderable="<%= true %>"
                    value="<%= backgroundTask.getCompletionDate() %>"
                />
                
                <%-- 
                <liferay-ui:search-container-column-text
                    name="executor-name"
                    value="<%= backgroundTask.getTaskExecutorClassName() %>"
                />
                --%>
                
                <liferay-ui:search-container-column-text
                    cssClass="table-cell-content"
                    name="download"
                >
    
                    <%
                    List<FileEntry> attachmentsFileEntries = backgroundTask.getAttachmentsFileEntries();
    
                    for (FileEntry fileEntry : attachmentsFileEntries) {
                    %>
    
                        <%
                        StringBundler sb = new StringBundler(4);
    
                        sb.append(fileEntry.getTitle());
                        sb.append(StringPool.OPEN_PARENTHESIS);
                        sb.append(TextFormatter.formatStorageSize(fileEntry.getSize(), locale));
                        sb.append(StringPool.CLOSE_PARENTHESIS);
                        %>
    
                        <liferay-ui:icon
                            iconCssClass="download"
                            label="<%= true %>"
                            markupView="lexicon"
                            message="<%= sb.toString() %>"
                            method="get"
                            url="<%= PortletFileRepositoryUtil.getDownloadPortletFileEntryURL(themeDisplay, fileEntry, StringPool.BLANK) %>"
                        />
    
                    <%
                    }
                    %>
    
                </liferay-ui:search-container-column-text>            
    
            </liferay-ui:search-container-row>
    
            <liferay-ui:search-iterator />
    
        </liferay-ui:search-container>
    </aui:form>
</div>
