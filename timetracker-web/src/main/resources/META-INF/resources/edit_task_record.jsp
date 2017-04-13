<%--
    edit_task_record.jsp: edit a single task-record.

    Created:     2013-10-07 10:41 by Christian Berndt
    Modified:    2017-04-04 23:57 by Christian Berndt
    Version:     1.5.9
--%>

<%@ include file="/init.jsp"%>

<%@page import="java.util.Calendar"%>

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
    // TODO: retrieve preferences like in configuration.jsp
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
    
    Calendar cal = Calendar.getInstance(); 
    Date now = new Date(); 
    cal.setTime(now); 

    int fromDateDay = cal.get(Calendar.DAY_OF_MONTH);  
    int fromDateMonth = cal.get(Calendar.MONTH); 
    int fromDateYear = cal.get(Calendar.YEAR);  
    int fromDateHour = cal.get(Calendar.HOUR_OF_DAY);
    int fromDateMinute = 0;

    int untilDateHour = fromDateHour; 
    int untilDateMinute = 15;

    if (taskRecord != null) {

        durationInMinutes =
            String.valueOf(taskRecord.getDurationInMinutes());

        Date fromDate = taskRecord.getStartDate();
        Date untilDate = taskRecord.getEndDate();
        
        if (fromDate != null) {
            
            cal.setTime(fromDate); 

            fromDateDay = cal.get(Calendar.DAY_OF_MONTH);
            fromDateMonth = cal.get(Calendar.MONTH); 
            fromDateYear = cal.get(Calendar.YEAR); 
            fromDateHour = cal.get(Calendar.HOUR_OF_DAY); 
            fromDateMinute = cal.get(Calendar.MINUTE); 
        }

        if (untilDate != null) {
            
            cal.setTime(untilDate); 
            
            untilDateHour = cal.get(Calendar.HOUR_OF_DAY); 
            untilDateMinute = cal.get(Calendar.MINUTE); 
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

                <aui:input name="untilDate" type="hidden"
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

                <aui:field-wrapper name="date">

                    <liferay-ui:input-date name="fromDate"
                        disabled="<%= !hasUpdatePermission %>"
                        dayParam="fromDateDay"
                        dayValue="<%=fromDateDay%>"
                        monthParam="fromDateMonth"
                        monthValue="<%=fromDateMonth%>"
                        yearParam="fromDateYear"
                        yearValue="<%=fromDateYear%>" />

                </aui:field-wrapper>

                <c:if test="<%=Validator.equals("from-until", timeFormat)%>">
                
                    <aui:field-wrapper cssClass="clearfix from-until" name="from-until">

                        <liferay-ui:input-time name="fromTime" 
                            disabled="<%= !hasUpdatePermission %>"
                            minuteInterval="<%= 15 %>"               
                            minuteParam="fromDateMinute"
                            minuteValue="<%= fromDateMinute %>"
                            amPmParam="fromDateAmPm"
                            hourParam="fromDateHour" 
                            hourValue="<%= fromDateHour %>"
                            timeFormat="24-hour" />
                            
                        <liferay-ui:input-time name="untilTime"
                            disabled="<%= !hasUpdatePermission %>"
                            minuteInterval="<%= 15 %>"
                            minuteParam="untilDateMinute"
                            minuteValue="<%= untilDateMinute %>"
                            amPmParam="untilDateAmPm"
                            hourParam="untilDateHour" 
                            hourValue="<%= untilDateHour %>" 
                            timeFormat="24-hour" />

                    </aui:field-wrapper>
                    
                </c:if>
                
                <c:if test="<%=!Validator.equals("from-until", timeFormat)%>">
                
                    <aui:field-wrapper label="duration"
                        helpMessage="duration-help">
                        
                        <%-- TODO: why this? --%>
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