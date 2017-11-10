<%--
    edit_task_record.jsp: edit a single task-record.

    Created:     2013-10-07 10:41 by Christian Berndt
    Modified:    2017-11-10 15:38 by Christian Berndt
    Version:     1.7.2
--%>

<%@ include file="/init.jsp"%>

<%
    TaskRecord taskRecord = (TaskRecord) request.getAttribute(TimetrackerWebKeys.TASK_RECORD);

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

    String title = LanguageUtil.get(request, "new-task-record");

    boolean hasUpdatePermission = false;
    boolean hasViewPermission = false;
    boolean hasDeletePermission = false;
    boolean hasPermissionsPermission = false;

    if (taskRecord != null) {

        durationInMinutes = String.valueOf(taskRecord.getDurationInMinutes());

        Date fromDate = taskRecord.getFromDate();
        Date untilDate = taskRecord.getUntilDate();

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

        title = LanguageUtil.format(request, "edit-task-record-x",
                String.valueOf(taskRecord.getTaskRecordId()));

        hasUpdatePermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
                TimetrackerActionKeys.UPDATE);
        hasViewPermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
                TimetrackerActionKeys.VIEW);
        hasDeletePermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
                TimetrackerActionKeys.DELETE);
        hasPermissionsPermission = TaskRecordPermission.contains(permissionChecker, taskRecord,
                TimetrackerActionKeys.PERMISSIONS);

    } else {

        taskRecord = TaskRecordServiceUtil.createTaskRecord();
        hasUpdatePermission = true;

    }

    String redirect = ParamUtil.getString(request, "redirect");

    String backURL = ParamUtil.getString(request, "backURL", redirect);

    portletDisplay.setShowBackIcon(true);
    portletDisplay.setURLBack(redirect);

    renderResponse.setTitle(title);

    request.setAttribute("showTitle", "true"); // used by inofix-theme
%>

<div class="container-fluid-1280">

    <portlet:actionURL name="updateTaskRecord" var="updateTaskRecordURL">
        <portlet:param name="mvcPath" value="/edit_task_record.jsp" />
    </portlet:actionURL>

    <aui:form method="post" action="<%=updateTaskRecordURL%>" name="fm">
    
        <aui:input name="cmd" type="hidden" 
            value="<%= Constants.UPDATE %>"/>
        <aui:input name="userId" type="hidden"
            value="<%=String.valueOf(themeDisplay.getUserId())%>" />
        
        <aui:model-context bean="<%=taskRecord%>"
            model="<%=TaskRecord.class%>" />
    
        <div class="lfr-form-content">
        
            <liferay-ui:error exception="<%= TaskRecordDurationException.class %>" message="please-enter-a-valid-duration" />
        
            <aui:fieldset-group markupView="<%= markupView %>">
 
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
    
    
                    <aui:field-wrapper name="date" label="date" required="true" cssClass="col-sm-6">
    
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
    
                        <aui:field-wrapper cssClass="clearfix col-sm-6 from-until"
                            label="from-until" name="from-until" required="true">
    
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
                                disabled="<%=!hasUpdatePermission%>" />
    
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
        
            </aui:fieldset-group>
        </div>
                           
        <aui:button-row>
            <aui:button cssClass="btn-lg" disabled="<%= !hasUpdatePermission %>" type="submit" />           
            <aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
        </aui:button-row>
        
    </aui:form>
</div>
