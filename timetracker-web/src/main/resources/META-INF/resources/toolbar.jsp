<%--
    toolbar.jsp: The toolbar of the timetracker portlet
    
    Created:    2016-03-20 16:58 by Christian Berndt
    Modified:   2017-11-10 16:51 by Christian Berndt
    Version:    1.2.8
--%>

<%@ include file="/init.jsp"%>

<%
    String orderByCol = ParamUtil.getString(request, "orderByCol", "modified-date");

    String orderByType = ParamUtil.getString(request, "orderByType", "desc");

    String searchContainerId = ParamUtil.getString(request, "searchContainerId");
    
    int total = GetterUtil.getInteger(request.getAttribute("view.jsp-total"));
    
    PortletURL portletURL = liferayPortletResponse.createRenderURL();
    portletURL.setParameters(renderRequest.getParameterMap());
%>

<liferay-frontend:management-bar
    disabled="<%= total == 0 %>"
    includeCheckBox="<%= true %>"
    searchContainerId="<%= searchContainerId %>"
>

    <liferay-frontend:management-bar-filters>
        <liferay-frontend:management-bar-sort
            orderByCol="<%= orderByCol %>"
            orderByType="<%= orderByType %>"
            orderColumns='<%= columns %>'
            portletURL="<%= portletURL %>"
        />
    </liferay-frontend:management-bar-filters>



    <liferay-frontend:management-bar-buttons>

        <liferay-ui:icon-menu cssClass="pull-left" direction="down"
            disabled="<%=total == 0%>" icon="icon-download"
            message="download" showWhenSingleIcon="true">
            <%
                for (int i = 0; i < exportNames.length; i++) {

                    String exportName = exportNames[i];

                    ResourceURL downloadURL = liferayPortletResponse.createResourceURL();

                    downloadURL.setResourceID("viewTaskRecord");

                    // Copy render parameters to resourceRequest
                    downloadURL.setParameters(renderRequest.getParameterMap());

                    downloadURL.setParameter("cmd", "download");
                    downloadURL.setParameter("idx", String.valueOf(i));
                    downloadURL.setParameter("start", "0");
                    downloadURL.setParameter("end", String.valueOf(Integer.MAX_VALUE));
            %>
            <liferay-ui:icon
                message="<%=exportName%>"
                url="<%=downloadURL.toString()%>" />
            <%
                }
            %>
        </liferay-ui:icon-menu>

        <liferay-util:include page="/display_style_buttons.jsp"
            servletContext="<%=application%>" />
    </liferay-frontend:management-bar-buttons>

    <liferay-frontend:management-bar-action-buttons>
    
        <liferay-ui:icon-menu cssClass="pull-left">
        <% // TODO %>
<%--             <liferay-ui:icon iconCssClass="icon-ok" message="approve" url="<%= portletURL.toString() %>" /> --%>
<%--             <liferay-ui:icon iconCssClass="icon-download" message="download" url="<%= portletURL.toString() %>" /> --%>
<%--             <liferay-ui:icon iconCssClass="icon-ban-circle" message="reject" url="<%= portletURL.toString() %>" /> --%>
            <liferay-ui:icon iconCssClass="icon-remove" message="delete" url="<%= "javascript:" + renderResponse.getNamespace() + "deleteEntries();" %>" />
        </liferay-ui:icon-menu>

    
        <%--    
        <liferay-frontend:management-bar-sidenav-toggler-button
            icon="info-circle"
            label="info"
        />
        --%>
    </liferay-frontend:management-bar-action-buttons>
</liferay-frontend:management-bar>

<aui:script>
    function <portlet:namespace />deleteEntries() {
        if (confirm('<%= UnicodeLanguageUtil.get(request, "are-you-sure-you-want-to-delete-the-selected-entries") %>')) {
            var form = AUI.$(document.<portlet:namespace />fm);

            form.attr('method', 'post');
            form.fm('<%= Constants.CMD %>').val('<%= Constants.DELETE %>');
            form.fm('deleteTaskRecordIds').val(Liferay.Util.listCheckedExcept(form, '<portlet:namespace />allRowIds'));

            submitForm(form);
        }
    }
</aui:script>
