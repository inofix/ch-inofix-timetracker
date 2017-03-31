<%--
    edit_task_record.jsp: edit a single task-record.

    Created:     2013-10-07 10:41 by Christian Berndt
    Modified:    2017-03-31 17:44 by Christian Berndt
    Version:     1.5.7
--%>

<%@ include file="/init.jsp"%>

<%
    String timeFormat = portletPreferences.getValue("time-format", "from-until");

    String redirect = ParamUtil.getString(request, "redirect");

    String backURL = ParamUtil.getString(request, "backURL", redirect);

    String windowId = "";
    windowId = ParamUtil.getString(request, "windowId");

    // Close the popup, if we are in popup mode, a redirect was provided
    // and the windowId is "editTaskRecord" (which means, viewByDefault 
    // is false.

    if (Validator.isNotNull(redirect) && themeDisplay.isStatePopUp() &&
        "editTaskRecord".equals(windowId)) {

        PortletURL closeURL = renderResponse.createRenderURL();
        closeURL.setParameter("mvcPath", "/close_popup.jsp");
        closeURL.setParameter("redirect", redirect);
        closeURL.setParameter("windowId", windowId);
        backURL = closeURL.toString();
    }

    String historyKey = ParamUtil.getString(request, "historyKey");

    String mvcPath = ParamUtil.getString(request, "mvcPath");

    // Retrieve the display settings.
    PortletPreferences preferences = renderRequest.getPreferences();

    String portletResource =
        ParamUtil.getString(request, "portletResource");

    if (Validator.isNotNull(portletResource)) {

        preferences =
            PortletPreferencesFactoryUtil.getPortletSetup(
                request, portletResource);
    }

    TaskRecord taskRecord =
        (TaskRecord) request.getAttribute(TimetrackerWebKeys.TASK_RECORD);

    String durationInMinutes = null;

    String namespace = portletDisplay.getNamespace();

    int startDateHour = -1;
    int startDateMinute = -1;
    String startDatePrefix = namespace + "startDate";

    int endDateHour = -1;
    int endDateMinute = -1;
    String endDatePrefix = namespace + "endDate";

    if (taskRecord != null) {

        durationInMinutes =
            String.valueOf(taskRecord.getDurationInMinutes());

        Date startDate = taskRecord.getStartDate();
        Date endDate = taskRecord.getEndDate();

        if (startDate != null) {
            startDateHour = startDate.getHours();
            startDateMinute = startDate.getMinutes();
        }

        if (endDate != null) {
            endDateHour = endDate.getHours();
            endDateMinute = endDate.getMinutes();
        }
    } else {
        
        // create an empty task record
        taskRecord = TaskRecordServiceUtil.createTaskRecord();
    }
    
    boolean hasUpdatePermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
            TaskRecordActionKeys.UPDATE);
    boolean hasViewPermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
            TaskRecordActionKeys.VIEW);
    boolean hasDeletePermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
            TaskRecordActionKeys.DELETE);
    boolean hasPermissionsPermission = TaskRecordPermission.contains(permissionChecker, taskRecord, 
            TaskRecordActionKeys.PERMISSIONS);
%>

<portlet:actionURL name="updateTaskRecord" var="updateTaskRecordURL"
    windowState="<%=LiferayWindowState.POP_UP.toString() %>">
    <portlet:param name="mvcPath" value="/edit_task_record.jsp" />
</portlet:actionURL>

<liferay-ui:header title="timetracker" backURL="<%=backURL%>"
    showBackURL="<%=true%>" />

<aui:form method="post" action="<%=updateTaskRecordURL%>" name="fm">

    <aui:input name="userId" type="hidden"
        value="<%=String.valueOf(themeDisplay.getUserId())%>" />

    <%-- The model for this record. --%>
    <aui:model-context bean="<%=taskRecord%>"
        model="<%=TaskRecord.class%>" />

    <aui:row>
        <aui:col span="6">
            <aui:fieldset>

                <aui:input name="backURL" type="hidden"
                    value="<%=backURL%>" />

                <aui:input name="endDate" type="hidden"
                    disabled="<%=!hasUpdatePermission%>" />

                <aui:input name="redirect" type="hidden"
                    value="<%=redirect%>" />

                <aui:input name="taskRecordId" type="hidden"
                    disabled="<%=!hasUpdatePermission%>" />

                <aui:input name="workPackage"
                    helpMessage="work-package-help"
                    cssClass="timetracker-input"
                    disabled="<%=!hasUpdatePermission%>" />

                <aui:input name="ticketURL" label="ticket-url"
                    helpMessage="ticket-url-help"
                    cssClass="timetracker-input"
                    disabled="<%=!hasUpdatePermission%>" />

                <aui:input name="description"
                    disabled="<%=!hasUpdatePermission%>"
                    helpMessage="description-help" />

            </aui:fieldset>
        </aui:col>

        <aui:col span="6">
            <aui:fieldset>

                <aui:input name="startDate" label="date" disabled="<%=!hasUpdatePermission%>"/>

                <c:if test="<%=Validator.equals("from-until", timeFormat)%>">
                    <aui:field-wrapper cssClass="clearfix from-until" name="from-until">
                    
                        <aui:input name="startTimeMinute" value="0" type="hidden" />
                        <aui:input name="startTimeHour" value="0" type="hidden"/>

                        <liferay-ui:input-time name="startTime" 
                            minuteInterval="<%= 15 %>"               
                            minuteParam="startTimeMinute"
                            minuteValue="<%= startDateMinute %>"
                            amPmParam="startTimeAmPm"
                            hourParam="startTimeHour" 
                            hourValue="<%= startDateHour %>"/>
                            
                        <aui:input name="endTimeMinute" value="0" type="hidden"/>
                        <aui:input name="endTimeHour" value="30" type="hidden" />
                            
                        <liferay-ui:input-time name="endTime"
                            minuteInterval="<%= 15 %>"
                            minuteParam="endTimeMinute"
                            minuteValue="<%= endDateMinute %>"
                            amPmParam="endTimeAmPm"
                            hourParam="endTimeHour" 
                            hourValue="<%= endDateHour %>" />

                    </aui:field-wrapper>
                </c:if>
                
                <c:if test="<%=!Validator.equals("from-until", timeFormat)%>">
                
                    <aui:field-wrapper label="duration"
                        helpMessage="duration-help">
                        
                        <input name="<portlet:namespace/>duration"
                            value="<%=durationInMinutes%>"
                            class="aui-field-input aui-field-input-text lfr-input-text duration-in-minutes" 
                            disabled="<%=!hasUpdatePermission%>"/>
                            
                    </aui:field-wrapper>
                    
                </c:if>

                <aui:select name="status" disabled="<%=!hasUpdatePermission%>">
                    <aui:option
                        value="<%=WorkflowConstants.STATUS_APPROVED%>"
                        selected="<%=WorkflowConstants.STATUS_APPROVED == taskRecord.getStatus()%>">
                        <liferay-ui:message key="approved" />
                    </aui:option>
                    <aui:option
                        value="<%=WorkflowConstants.STATUS_DENIED%>"
                        selected="<%=WorkflowConstants.STATUS_DENIED == taskRecord.getStatus()%>">
                        <liferay-ui:message key="denied" />
                    </aui:option>
                    <aui:option
                        value="<%=WorkflowConstants.STATUS_DRAFT%>"
                        selected="<%=WorkflowConstants.STATUS_DRAFT == taskRecord.getStatus()%>">
                        <liferay-ui:message key="draft" />
                    </aui:option>
                    <aui:option
                        value="<%=WorkflowConstants.STATUS_INACTIVE%>"
                        selected="<%=WorkflowConstants.STATUS_INACTIVE == taskRecord.getStatus()%>">
                        <liferay-ui:message key="inactive" />
                    </aui:option>
                    <aui:option
                        value="<%=WorkflowConstants.STATUS_INCOMPLETE%>"
                        selected="<%=WorkflowConstants.STATUS_INCOMPLETE == taskRecord.getStatus()%>">
                        <liferay-ui:message key="incomplete" />
                    </aui:option>
                    <aui:option
                        value="<%=WorkflowConstants.STATUS_PENDING%>"
                        selected="<%=WorkflowConstants.STATUS_PENDING == taskRecord.getStatus()%>">
                        <liferay-ui:message key="pending" />
                    </aui:option>
                </aui:select>

            </aui:fieldset>

            <aui:button-row>
                <aui:button type="submit" disabled="<%=!hasUpdatePermission%>"/>
            </aui:button-row>

        </aui:col>
    </aui:row>
</aui:form>

<%-- 
<ifx-util:build-info />
--%>