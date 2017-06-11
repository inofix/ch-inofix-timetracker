<%--
    export_import_toolbar.jsp: the export-import toolbar.
    
    Created:    2017-05-16 17:30 by Christian Berndt
    Modified:   2017-06-07 00:52 by Christian Berndt
    Version:    1.0.3
--%>

<%@ include file="/init.jsp" %>

<%
    long groupId = ParamUtil.getLong(request, "groupId");
    String displayStyle = ParamUtil.getString(request, "displayStyle", "descriptive");
    String orderByCol = ParamUtil.getString(request, "orderByCol");
    String orderByType = ParamUtil.getString(request, "orderByType");
    String navigation = ParamUtil.getString(request, "navigation", "all");
    String searchContainerId = ParamUtil.getString(request, "searchContainerId");
    String section = ParamUtil.getString(request, "section");
    
    PortletURL portletURL = renderResponse.createRenderURL();

    portletURL.setParameter("groupId", String.valueOf(groupId));
    portletURL.setParameter("displayStyle", displayStyle);
    portletURL.setParameter("mvcPath", "/view.jsp"); 
    portletURL.setParameter("navigation", navigation);
    portletURL.setParameter("orderByCol", orderByCol);
    portletURL.setParameter("orderByType", orderByType);
    portletURL.setParameter("searchContainerId", String.valueOf(searchContainerId));
    portletURL.setParameter("section", section);
%>

<liferay-frontend:management-bar
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