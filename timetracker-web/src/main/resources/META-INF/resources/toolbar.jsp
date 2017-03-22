<%--
    toolbar.jsp: The toolbar of the timetracker portlet
    
    Created:    2016-03-20 16:58 by Christian Berndt
    Modified:   2017-03-22 15:11 by Christian Berndt
    Version:    1.1.1
--%>

<%@ include file="/init.jsp"%>

<%
    TaskRecordSearch searchContainer = new TaskRecordSearch(liferayPortletRequest, portletURL);

    TaskRecordDisplayTerms displayTerms = (TaskRecordDisplayTerms) searchContainer.getDisplayTerms();

    long companyId = themeDisplay.getCompanyId();

    Sort sort = SortFactoryUtil.getSort(User.class, "lastName", "asc");

    int numUsers = UserLocalServiceUtil.searchCount(companyId, null, WorkflowConstants.STATUS_APPROVED, null);

    Hits hits = UserLocalServiceUtil.search(companyId, null, WorkflowConstants.STATUS_APPROVED, null, 0,
            numUsers, sort);

    List<Document> documents = hits.toList();

    boolean ignoreEndDate = ParamUtil.getBoolean(request, "ignoreEndDate", true);
    boolean ignoreStartDate = ParamUtil.getBoolean(request, "ignoreStartDate", true);
    int status = ParamUtil.getInteger(request, "status", WorkflowConstants.STATUS_ANY);
%>

<aui:nav-bar>

    <aui:nav id="toolbarContainer" cssClass="pull-left toolbar-container">

        <aui:nav-item cssClass="hide" dropdown="<%=true%>"
            id="actionsButtonContainer" label="actions">

            <%
                String downloadTaskRecordsURL = "javascript:" + renderResponse.getNamespace()
                                    + "downloadTaskRecords();";
            %>

            <%-- <aui:nav-item href="<%=downloadTaskRecordsURL%>" iconCssClass="icon-download"
                label="download-selected-task-records" /> --%>

            <%
                String deleteTaskRecordsURL = "javascript:" + renderResponse.getNamespace()
                                    + "editSet('delete');";
            %>

            <aui:nav-item>
                <liferay-ui:icon-delete url="<%=deleteTaskRecordsURL%>"
                    label="true" message="delete-selected-task-records" />
            </aui:nav-item>
        </aui:nav-item>

        <portlet:renderURL var="editURL"
            windowState="<%= LiferayWindowState.POP_UP.toString() %>">
            <portlet:param name="redirect" value="<%= currentURL %>" />
            <portlet:param name="mvcPath" value="/edit_task_record.jsp" />
            <portlet:param name="windowId" value="editTaskRecord" />
        </portlet:renderURL>

        <%
            // TODO: enable permission checks
        %>
        <%-- 
        <c:if test='<%=TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
                                ActionKeys.ADD_TASK_RECORD)%>'>
        --%>
        <%
        
            String taglibEditURL = "javascript:Liferay.Util.openWindow({id: '" + renderResponse.getNamespace() + "editTaskRecord', title: '" + HtmlUtil.escapeJS(LanguageUtil.format(request, "edit-x", "new")) + "', uri:'" + HtmlUtil.escapeJS(editURL) + "'});";

        %>

        <aui:nav-item>
            <aui:a label="add-task-record" title="add-task-record"
                href="<%=taglibEditURL%>"
                cssClass="btn btn-primary add-task-record" />
        </aui:nav-item>

        <%-- 
        </c:if>
        --%>

        <aui:nav-item dropdown="<%=true%>" id="exportButtonContainer"
            label="export">

            <%
                ResourceURL resourceURL = liferayPortletResponse.createResourceURL();

                resourceURL.setResourceID("exportTaskRecords");
                resourceURL.setParameters(renderRequest.getParameterMap());

                String exportURL = resourceURL.toString();

                String ns = portletDisplay.getNamespace();
            %>

            <aui:nav-item>
                <liferay-ui:icon
                    url='<%=HttpUtil.addParameter(exportURL, ns + "format", "csv")%>'
                    iconCssClass="icon-download-alt" label="true"
                    message="export-csv" />
            </aui:nav-item>

            <aui:nav-item>
                <liferay-ui:icon
                    url='<%=HttpUtil.addParameter(exportURL, ns + "format", "fulllatex")%>'
                    iconCssClass="icon-download-alt" label="true"
                    message="export-latex" />
            </aui:nav-item>

            <aui:nav-item>
                <liferay-ui:icon
                    url='<%=HttpUtil.addParameter(exportURL, ns + "format", "xml")%>'
                    iconCssClass="icon-download-alt" label="true"
                    message="export-xml" />
            </aui:nav-item>
        </aui:nav-item>
    </aui:nav>
    
    <aui:nav cssClass="pull-right">
            
        <portlet:renderURL var="clearURL" />
    
        <aui:button value="reset" href="<%=clearURL%>"
            cssClass="clear-btn" />
    </aui:nav>

    <aui:nav-bar-search cssClass="pull-right">

        <liferay-portlet:renderURL varImpl="searchURL" />

        <aui:form action="<%=searchURL%>" method="get" name="fm1">

            <liferay-portlet:renderURLParams varImpl="searchURL" />

            <liferay-ui:search-toggle buttonLabel="search"
                displayTerms="<%=displayTerms%>"
                id="toggle_id_timetracker_search">

                <aui:fieldset>
                    <aui:input
                        name="<%=TaskRecordSearchTerms.WORK_PACKAGE%>"
                        value="<%=displayTerms.getWorkPackage()%>"
                        inlineField="true" />
                    <aui:input
                        name="<%=TaskRecordSearchTerms.DESCRIPTION%>"
                        value="<%=displayTerms.getDescription()%>"
                        inlineField="true" />

                    <aui:input
                        dateTogglerCheckboxLabel="ignore-start-date"
                        disabled="<%=ignoreStartDate%>" formName="fm"
                        name="startDate" model="<%=TaskRecord.class%>"
                        inlineField="true" />

                    <aui:input
                        dateTogglerCheckboxLabel="ignore-end-date"
                        disabled="<%=ignoreEndDate%>" formName="fm"
                        name="endDate" model="<%=TaskRecord.class%>"
                        inlineField="true" />

                    <aui:select name="userId" inlineField="true">
                        <aui:option value="-1">
                            <liferay-ui:message key="any-user" />
                        </aui:option>
                        <c:forEach items="<%=documents%>" var="document">
                            <aui:option
                                value="${document.get('userId')}">${document.get('fullName')}</aui:option>
                        </c:forEach>
                    </aui:select>

                    <aui:select name="status" inlineField="true"
                        last="true">
                        <aui:option
                            value="<%=WorkflowConstants.STATUS_ANY%>"
                            selected="<%=WorkflowConstants.STATUS_ANY == status%>">
                            <liferay-ui:message key="any" />
                        </aui:option>
                        <aui:option
                            value="<%=WorkflowConstants.STATUS_APPROVED%>"
                            selected="<%=WorkflowConstants.STATUS_APPROVED == status%>">
                            <liferay-ui:message key="approved" />
                        </aui:option>
                        <aui:option
                            value="<%=WorkflowConstants.STATUS_DENIED%>"
                            selected="<%=WorkflowConstants.STATUS_DENIED == status%>">
                            <liferay-ui:message key="denied" />
                        </aui:option>
                        <aui:option
                            value="<%=WorkflowConstants.STATUS_DRAFT%>"
                            selected="<%=WorkflowConstants.STATUS_DRAFT == status%>">
                            <liferay-ui:message key="draft" />
                        </aui:option>
                        <aui:option
                            value="<%=WorkflowConstants.STATUS_INACTIVE%>"
                            selected="<%= WorkflowConstants.STATUS_INACTIVE == status %>">
                            <liferay-ui:message key="inactive" />
                        </aui:option>
                        <aui:option
                            value="<%= WorkflowConstants.STATUS_INCOMPLETE %>"
                            selected="<%= WorkflowConstants.STATUS_INCOMPLETE == status %>">
                            <liferay-ui:message key="incomplete" />
                        </aui:option>
                        <aui:option
                            value="<%= WorkflowConstants.STATUS_PENDING %>"
                            selected="<%= WorkflowConstants.STATUS_PENDING == status %>">
                            <liferay-ui:message key="pending" />
                        </aui:option>
                    </aui:select>

                </aui:fieldset>
            </liferay-ui:search-toggle>

        </aui:form>

    </aui:nav-bar-search>

</aui:nav-bar>
