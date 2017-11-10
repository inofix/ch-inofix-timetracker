<%--
    import_task_records_processes.jsp: list of import processes
    
    Created:    2017-06-08 00:21 by Christian Berndt
    Modified:   2017-11-10 17:28 by Christian Berndt
    Version:    1.0.5
--%>

<%@ include file="/init.jsp" %>

<%
    long groupId = ParamUtil.getLong(request, "groupId");
    String displayStyle = ParamUtil.getString(request, "displayStyle");
    String navigation = ParamUtil.getString(request, "navigation");
    String orderByCol = ParamUtil.getString(request, "orderByCol");
    String orderByType = ParamUtil.getString(request, "orderByType");
    String searchContainerId = ParamUtil.getString(request, "searchContainerId");
    
    PortletURL portletURL = liferayPortletResponse.createRenderURL();
    
    portletURL.setParameter("groupId", String.valueOf(groupId));
    portletURL.setParameter("displayStyle", displayStyle);
    portletURL.setParameter("navigation", navigation);
    portletURL.setParameter("orderByCol", orderByCol);
    portletURL.setParameter("orderByType", orderByType);
    portletURL.setParameter("searchContainerId", searchContainerId);
    
    OrderByComparator<BackgroundTask> orderByComparator = BackgroundTaskComparatorFactoryUtil.getBackgroundTaskOrderByComparator(orderByCol, orderByType);
%>

<portlet:actionURL name="importTaskRecords" var="deleteBackgroundTasksURL"/>

<aui:form action="<%= deleteBackgroundTasksURL %>" method="get" name="fm">

    <aui:input name="<%= Constants.CMD %>" type="hidden" />
    <aui:input name="deleteBackgroundTaskIds" type="hidden" />
    <aui:input name="redirect" type="hidden" value="<%= currentURL.toString() %>" />
    <aui:input name="tabs1" type="hidden" value="<%= tabs1 %>"/>
    <aui:input name="tabs2" type="hidden" value="<%= tabs2 %>"/>
    
    <liferay-ui:search-container
        emptyResultsMessage="no-import-processes-were-found"
        id="<%= searchContainerId %>"
        iteratorURL="<%= portletURL %>"
        orderByCol="<%= orderByCol %>"
        orderByComparator="<%= orderByComparator %>"
        orderByType="<%= orderByType %>"
        rowChecker="<%= new EmptyOnClickRowChecker(liferayPortletResponse) %>"
    >
        <liferay-ui:search-container-results>

            <%
                int backgroundTasksCount = 0;
                List<BackgroundTask> backgroundTasks = null;

                if (navigation.equals("all")) {
                    backgroundTasksCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(groupId,
                            TaskRecordImportBackgroundTaskExecutor.class.getName());
                    backgroundTasks = BackgroundTaskManagerUtil.getBackgroundTasks(groupId,
                            TaskRecordImportBackgroundTaskExecutor.class.getName(), searchContainer.getStart(),
                            searchContainer.getEnd(), searchContainer.getOrderByComparator());
                } else {
                    boolean completed = false;

                    if (navigation.equals("completed")) {
                        completed = true;
                    }

                    backgroundTasksCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(groupId,
                            TaskRecordImportBackgroundTaskExecutor.class.getName(), completed);
                    backgroundTasks = BackgroundTaskManagerUtil.getBackgroundTasks(groupId,
                            TaskRecordImportBackgroundTaskExecutor.class.getName(), completed,
                            searchContainer.getStart(), searchContainer.getEnd(),
                            searchContainer.getOrderByComparator());
                }

                searchContainer.setResults(backgroundTasks);
                searchContainer.setTotal(backgroundTasksCount);
            %>

        </liferay-ui:search-container-results>

        <liferay-ui:search-container-row
            className="com.liferay.portal.kernel.backgroundtask.BackgroundTask"
            keyProperty="backgroundTaskId"
            modelVar="backgroundTask"
        >

            <%
            String backgroundTaskName = backgroundTask.getName();

            if (backgroundTaskName.equals(StringPool.BLANK)) {
                backgroundTaskName = LanguageUtil.get(request, "untitled");
            }
            %>

            <c:choose>
                <c:when test='<%= displayStyle.equals("descriptive") %>'>
                    <liferay-ui:search-container-column-text>
                        <liferay-ui:user-portrait
                            cssClass="user-icon-lg"
                            userId="<%= backgroundTask.getUserId() %>"
                        />
                    </liferay-ui:search-container-column-text>

                    <liferay-ui:search-container-column-text
                        colspan="<%= 2 %>"
                    >

                        <%
                        User backgroundTaskUser = UserLocalServiceUtil.getUser(backgroundTask.getUserId());

                        Date createDate = backgroundTask.getCreateDate();

                        String modifiedDateDescription = LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - createDate.getTime(), true);
                        %>

                        <h6 class="text-default">
                            <liferay-ui:message arguments="<%= new String[] {HtmlUtil.escape(backgroundTaskUser.getFullName()), modifiedDateDescription} %>" key="x-modified-x-ago" />
                        </h6>

                        <h5>
                            <span id="<portlet:namespace />backgroundTaskName<%= backgroundTask.getBackgroundTaskId() %>">
                                <%= HtmlUtil.escape(backgroundTaskName) %>
                            </span>

                            <%
                            List<FileEntry> attachmentsFileEntries = backgroundTask.getAttachmentsFileEntries();

                            for (FileEntry fileEntry : attachmentsFileEntries) {
                            %>

                                <liferay-ui:icon
                                    icon="download"
                                    markupView="<%= markupView %>"
                                    method="get"
                                    url="<%= PortletFileRepositoryUtil.getDownloadPortletFileEntryURL(themeDisplay, fileEntry, StringPool.BLANK) %>"
                                />

                            <%
                            }
                            %>

                        </h5>

                        <c:if test="<%= backgroundTask.isInProgress() %>">

                            <%
                            BackgroundTaskStatus backgroundTaskStatus = BackgroundTaskStatusRegistryUtil.getBackgroundTaskStatus(backgroundTask.getBackgroundTaskId());
                            %>

                            <c:if test="<%= backgroundTaskStatus != null %>">

                                <%
                                int percentage = 100;

                                long allModelAdditionCountersTotal = GetterUtil.getLong(backgroundTaskStatus.getAttribute("allModelAdditionCountersTotal"));
                                long allPortletAdditionCounter = GetterUtil.getLong(backgroundTaskStatus.getAttribute("allPortletAdditionCounter"));
                                long currentModelAdditionCountersTotal = GetterUtil.getLong(backgroundTaskStatus.getAttribute("currentModelAdditionCountersTotal"));
                                long currentPortletAdditionCounter = GetterUtil.getLong(backgroundTaskStatus.getAttribute("currentPortletAdditionCounter"));

                                long allProgressBarCountersTotal = allModelAdditionCountersTotal + allPortletAdditionCounter;
                                long currentProgressBarCountersTotal = currentModelAdditionCountersTotal + currentPortletAdditionCounter;

                                if (allProgressBarCountersTotal > 0) {
                                    percentage = Math.round((float)currentProgressBarCountersTotal / allProgressBarCountersTotal * 100);
                                }
                                %>

                                <div class="active progress progress-striped progress-xs">
                                    <div class="progress-bar" style="width: <%= percentage %>%;">
                                        <c:if test="<%= allProgressBarCountersTotal > 0 %>">
                                            <%= percentage + StringPool.PERCENT %>
                                        </c:if>
                                    </div>
                                </div>

                                <%
                                String stagedModelName = (String)backgroundTaskStatus.getAttribute("stagedModelName");
                                String stagedModelType = (String)backgroundTaskStatus.getAttribute("stagedModelType");
                                %>

                                <c:if test="<%= Validator.isNotNull(stagedModelName) && Validator.isNotNull(stagedModelType) %>">
                                    <div class="progress-current-item">
                                        <strong><liferay-ui:message key="exporting" /><%= StringPool.TRIPLE_PERIOD %></strong> <%= ResourceActionsUtil.getModelResource(locale, stagedModelType) %> <em><%= HtmlUtil.escape(stagedModelName) %></em>
                                    </div>
                                </c:if>
                            </c:if>
                        </c:if>

                        <h6 class="background-task-status-row background-task-status-<%= BackgroundTaskConstants.getStatusLabel(backgroundTask.getStatus()) %> <%= BackgroundTaskConstants.getStatusCssClass(backgroundTask.getStatus()) %>">
                            <liferay-ui:message key="<%= backgroundTask.getStatusLabel() %>" />
                        </h6>

                        <c:if test="<%= Validator.isNotNull(backgroundTask.getStatusMessage()) %>">
                            <h6 class="background-task-status-row">
                                <a class="details-link" href="javascript:Liferay.fire('<portlet:namespace />viewBackgroundTaskDetails', {nodeId: 'backgroundTaskStatusMessage<%= backgroundTask.getBackgroundTaskId() %>', title: $('#<portlet:namespace />backgroundTaskName<%= backgroundTask.getBackgroundTaskId() %>').text()}); void(0);"><liferay-ui:message key="see-more-details" /></a>
                            </h6>

                            <div class="background-task-status-message hide" id="<portlet:namespace />backgroundTaskStatusMessage<%= backgroundTask.getBackgroundTaskId() %>">
                                <liferay-util:include page="/import_process_message_task_details.jsp" servletContext="<%= application %>">
                                    <liferay-util:param name="backgroundTaskId" value="<%= String.valueOf(backgroundTask.getBackgroundTaskId()) %>" />
                                </liferay-util:include>
                            </div>
                        </c:if>
                    </liferay-ui:search-container-column-text>
                </c:when>
                <c:when test='<%= displayStyle.equals("list") %>'>
                    <liferay-ui:search-container-column-text
                        name="user"
                    >
                        <liferay-ui:user-display
                            displayStyle="3"
                            showUserDetails="<%= false %>"
                            showUserName="<%= false %>"
                            userId="<%= backgroundTask.getUserId() %>"
                        />
                    </liferay-ui:search-container-column-text>

                    <liferay-ui:search-container-column-text
                        cssClass="table-cell-content"
                        name="title"
                    >
                        <span id="<%= liferayPortletResponse.getNamespace() + "backgroundTaskName" + String.valueOf(backgroundTask.getBackgroundTaskId()) %>">
                            <liferay-ui:message key="<%= HtmlUtil.escape(backgroundTaskName) %>" />
                        </span>
                    </liferay-ui:search-container-column-text>

                    <liferay-ui:search-container-column-jsp
                        name="status"
                        path="/import_process_message.jsp"
                    />

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
                                markupView="<%= markupView %>"
                                message="<%= sb.toString() %>"
                                method="get"
                                url="<%= PortletFileRepositoryUtil.getDownloadPortletFileEntryURL(themeDisplay, fileEntry, StringPool.BLANK) %>"
                            />

                        <%
                        }
                        %>

                    </liferay-ui:search-container-column-text>
                </c:when>
            </c:choose>

            <liferay-ui:search-container-column-text>
                <c:if test="<%= !backgroundTask.isInProgress() %>">
                    <liferay-ui:icon-menu direction="left-side" icon="<%= StringPool.BLANK %>" markupView="<%= markupView %>" message="<%= StringPool.BLANK %>" showWhenSingleIcon="<%= true %>">
                        <portlet:actionURL name="importTaskRecords" var="relaunchURL">
                            <portlet:param name="<%= Constants.CMD %>" value="<%= Constants.RELAUNCH %>" />
                            <portlet:param name="redirect" value="<%= portletURL.toString() %>" />
                            <portlet:param name="backgroundTaskId" value="<%= String.valueOf(backgroundTask.getBackgroundTaskId()) %>" />
                        </portlet:actionURL>

                        <liferay-ui:icon icon="reload" markupView="<%= markupView %>" message="relaunch" url="<%= relaunchURL %>" />

                        <portlet:actionURL name="importTaskRecords" var="deleteBackgroundTaskURL">
                            <portlet:param name="<%= Constants.CMD %>" value="deleteBackgroundTasks" />                            
                            <portlet:param name="redirect" value="<%= portletURL.toString() %>" />
                            <portlet:param name="deleteBackgroundTaskIds" value="<%= String.valueOf(backgroundTask.getBackgroundTaskId()) %>" />
                        </portlet:actionURL>

                        <%
                        Date completionDate = backgroundTask.getCompletionDate();
                        %>

                        <liferay-ui:icon-delete
                            label="<%= true %>"
                            message='<%= ((completionDate != null) && completionDate.before(new Date())) ? "clear" : "cancel" %>'
                            url="<%= deleteBackgroundTaskURL %>"
                        />
                    </liferay-ui:icon-menu>
                </c:if>
            </liferay-ui:search-container-column-text>
        </liferay-ui:search-container-row>

        <liferay-ui:search-iterator displayStyle="<%= displayStyle %>" markupView="<%= markupView %>" />
    </liferay-ui:search-container>
</aui:form>
