<%--
    import_process_message_task_details.jsp: display the status of the respective background task.
    
    Created:     2017-11-10 17:46 by Christian Berndt
    Modified:    2017-11-10 17:46 by Christian Berndt
    Version:     1.0.0
--%>

<%-- 
<%@ include file="/init.jsp" %>

<%
    ResultRow row = (ResultRow) request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

    BackgroundTask backgroundTask = (BackgroundTask) row.getObject();
%>

<h5 class="background-task-status-<%= BackgroundTaskConstants.getStatusLabel(backgroundTask.getStatus()) %> <%= BackgroundTaskConstants.getStatusCssClass(backgroundTask.getStatus()) %>">
    <liferay-ui:message key="<%= backgroundTask.getStatusLabel() %>" />
</h5>

<c:if test="<%=backgroundTask.isInProgress()%>">

    <%
        BackgroundTaskStatus backgroundTaskStatus = BackgroundTaskStatusRegistryUtil
                    .getBackgroundTaskStatus(backgroundTask.getBackgroundTaskId());
    %>

    <c:if test="<%=backgroundTaskStatus != null%>">

        <%
            Map<String, Serializable> taskContextMap = backgroundTask.getTaskContextMap();

            String cmd = (String) taskContextMap.get(Constants.CMD);

            int percentage = 100;

            long allModelAdditionCountersTotal = GetterUtil
                    .getLong(backgroundTaskStatus.getAttribute("allModelAdditionCountersTotal"));
            long allPortletAdditionCounter = GetterUtil
                    .getLong(backgroundTaskStatus.getAttribute("allPortletAdditionCounter"));
            long currentModelAdditionCountersTotal = GetterUtil
                    .getLong(backgroundTaskStatus.getAttribute("currentModelAdditionCountersTotal"));
            long currentPortletAdditionCounter = GetterUtil
                    .getLong(backgroundTaskStatus.getAttribute("currentPortletAdditionCounter"));

            long allProgressBarCountersTotal = allModelAdditionCountersTotal + allPortletAdditionCounter;
            long currentProgressBarCountersTotal = currentModelAdditionCountersTotal
                    + currentPortletAdditionCounter;

            if (allProgressBarCountersTotal > 0) {
                int base = 100;

                String phase = GetterUtil.getString(backgroundTaskStatus.getAttribute("phase"));

                if (phase.equals(Constants.EXPORT) && !Objects.equals(cmd, Constants.PUBLISH_TO_REMOTE)) {
                    base = 50;
                }

                percentage = Math
                        .round((float) currentProgressBarCountersTotal / allProgressBarCountersTotal * base);
            }
        %>

        <div class="active progress progress-striped progress-xs">
            <div class="progress-bar" style="width: <%=percentage%>%;">
                <c:if
                    test="<%=(allProgressBarCountersTotal > 0)
                                && (!Objects.equals(cmd, Constants.PUBLISH_TO_REMOTE) || (percentage < 100))%>">
                    <%=percentage + StringPool.PERCENT%>
                </c:if>
            </div>
        </div>

        <%
            String stagedModelName = (String) backgroundTaskStatus.getAttribute("stagedModelName");
                    String stagedModelType = (String) backgroundTaskStatus.getAttribute("stagedModelType");
        %>

        <c:choose>
            <c:when test="<%=Objects.equals(cmd, Constants.PUBLISH_TO_REMOTE) && (percentage == 100)%>">
                <div class="progress-current-item">
                    <strong><liferay-ui:message key="please-wait-as-the-publication-processes-on-the-remote-site" /></strong>
                </div>
            </c:when>
            <c:when test="<%=Validator.isNotNull(stagedModelName) && Validator.isNotNull(stagedModelType)%>">

                <%
                    String messageKey = "exporting";

                        if (Objects.equals(cmd, Constants.IMPORT)) {
                            messageKey = "importing";
                        } else if (Objects.equals(cmd, Constants.PUBLISH_TO_LIVE)
                                || Objects.equals(cmd, Constants.PUBLISH_TO_REMOTE)) {
                            messageKey = "publishing";
                        }
                %>

                <div class="progress-current-item">
                    <strong><liferay-ui:message
                            key="<%=messageKey%>" /><%=StringPool.TRIPLE_PERIOD%></strong>
                    <%=ResourceActionsUtil.getModelResource(locale, stagedModelType)%>
                    <em><%= HtmlUtil.escape(stagedModelName) %></em>
                </div>
            </c:when>
        </c:choose>
    </c:if>
</c:if>

<c:if test="<%=Validator.isNotNull(backgroundTask.getStatusMessage())%>">
    <h5>
        <a href="javascript:Liferay.fire('<portlet:namespace />viewBackgroundTaskDetails', {nodeId: 'backgroundTaskStatusMessage<%=backgroundTask.getBackgroundTaskId()%>', title: $('#<portlet:namespace />backgroundTaskName<%=backgroundTask.getBackgroundTaskId()%>').text()}); void(0);"><liferay-ui:message
                key="see-more-details" /></a>
    </h5>

    <div class="background-task-status-message hide"
        id="<portlet:namespace />backgroundTaskStatusMessage<%=backgroundTask.getBackgroundTaskId()%>">
        <liferay-util:include
            page="/import_process_message_task_details.jsp"
            servletContext="<%=application%>">
            <liferay-util:param name="backgroundTaskId"
                value="<%=String.valueOf(backgroundTask.getBackgroundTaskId())%>" />
        </liferay-util:include>
    </div>
</c:if>
--%>
TODO: re-enable import_process_message_task_details.jsp

