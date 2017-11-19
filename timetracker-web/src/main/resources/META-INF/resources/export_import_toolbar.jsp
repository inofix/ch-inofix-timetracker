<%--
    export_import_toolbar.jsp: the export-import toolbar.
    
    Created:    2017-05-16 17:30 by Christian Berndt
    Modified:   2017-11-18 19:17 by Christian Berndt
    Version:    1.0.7
--%>

<%@ include file="/init.jsp" %>

<%
    long groupId = ParamUtil.getLong(request, "groupId");
    String displayStyle = ParamUtil.getString(request, "displayStyle", "descriptive");
    String orderByCol = ParamUtil.getString(request, "orderByCol");
    String orderByType = ParamUtil.getString(request, "orderByType");
    String navigation = ParamUtil.getString(request, "navigation", "all");
    String searchContainerId = ParamUtil.getString(request, "searchContainerId");
    
    PortletURL portletURL = liferayPortletResponse.createRenderURL();

    portletURL.setParameter("groupId", String.valueOf(groupId));
    portletURL.setParameter("displayStyle", displayStyle);
    portletURL.setParameter("mvcPath", "/view.jsp"); 
    portletURL.setParameter("navigation", navigation);
    portletURL.setParameter("orderByCol", orderByCol);
    portletURL.setParameter("orderByType", orderByType);
    portletURL.setParameter("searchContainerId", String.valueOf(searchContainerId));
    portletURL.setParameter("tabs1", tabs1);
    portletURL.setParameter("tabs2", tabs2);
    
    int backgroundTasksCount = 0;
    String taskExecutorName = TaskRecordExportBackgroundTaskExecutor.class.getName(); 
    
    if ("import".equals(tabs2)) {
        taskExecutorName = TaskRecordImportBackgroundTaskExecutor.class.getName();         
    }

    if (navigation.equals("all")) {
        backgroundTasksCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(groupId, taskExecutorName);
    }
    else {
        boolean completed = false;

        if (navigation.equals("completed")) {
            completed = true;
        }
        backgroundTasksCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(groupId, taskExecutorName, completed);
    }
%>

<liferay-frontend:management-bar
    disabled="<%= backgroundTasksCount == 0 %>"
    includeCheckBox="<%= true %>"
    searchContainerId="<%= searchContainerId %>">
           
    <liferay-frontend:management-bar-filters>
    
        <liferay-frontend:management-bar-navigation
            navigationKeys='<%= new String[] {"all", "completed", "in-progress"} %>'
            navigationParam="navigation"
            portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
        />

        <liferay-frontend:management-bar-sort
            orderByCol="<%= orderByCol %>"
            orderByType="<%= orderByType %>"
            orderColumns='<%= new String[] {"create-date", "completion-date", "name"} %>'
            portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
        />
    </liferay-frontend:management-bar-filters>

    <liferay-frontend:management-bar-buttons>
        <liferay-frontend:management-bar-display-buttons
            displayViews='<%= new String[] {"descriptive", "list"} %>'
            portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
            selectedDisplayStyle="<%= displayStyle %>"
        />
    </liferay-frontend:management-bar-buttons>

    <liferay-frontend:management-bar-action-buttons>
        <liferay-frontend:management-bar-button href='<%= "javascript:" + liferayPortletResponse.getNamespace() + "deleteEntries();" %>' icon="times" label="delete" />
    </liferay-frontend:management-bar-action-buttons>
</liferay-frontend:management-bar>

<aui:script>
    function <portlet:namespace />deleteEntries() {
        if (confirm('<%= UnicodeLanguageUtil.get(request, "are-you-sure-you-want-to-delete-the-selected-entries") %>')) {
            var form = AUI.$(document.<portlet:namespace />fm);

            form.attr('method', 'post');
            form.fm('<%= Constants.CMD %>').val('<%= Constants.DELETE %>');
            form.fm('deleteBackgroundTaskIds').val(Liferay.Util.listCheckedExcept(form, '<portlet:namespace />allRowIds'));

            submitForm(form);
        }
    }
</aui:script>
