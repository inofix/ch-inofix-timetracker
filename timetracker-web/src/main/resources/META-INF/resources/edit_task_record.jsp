<%--
    edit_task_record.jsp: edit a single task-record.

    Created:     2013-10-07 10:41 by Christian Berndt
    Modified:    2016-11-27 20:19 by Christian Berndt
    Version:     1.5.2
--%>

<%@ include file="/init.jsp"%>

<%
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
        closeURL.setParameter("mvcPath", "/html/close_popup.jsp");
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
    }
%>

<portlet:actionURL name="updateTaskRecord" var="updateTaskRecordURL"
    windowState="<%=LiferayWindowState.POP_UP.toString() %>">
    <portlet:param name="mvcPath" value="/edit_task_record.jsp" />
</portlet:actionURL>

<div class="timetracker-portlet">

    <liferay-ui:header title="timetracker" backURL="<%=backURL%>"
        showBackURL="<%=true%>" />

    <aui:form method="post" action="<%=updateTaskRecordURL%>" name="fm">

        <aui:input name="userId"
            value="<%=String.valueOf(themeDisplay.getUserId())%>"
            type="hidden" />

        <%-- The model for this record. --%>
        <aui:model-context bean="<%=taskRecord%>"
            model="<%=TaskRecord.class%>" />

        <aui:row>
            <aui:col span="6">
                <aui:fieldset>

                    <aui:input name="backURL" type="hidden"
                        value="<%=backURL%>" />
                        
                    <aui:input name="endDate"
                        type="hidden" />
                        
                    <aui:input name="redirect" type="hidden"
                        value="<%=redirect%>" />
                        
                    <aui:input
                        name="taskRecordId"
                        type="hidden" />

                    <aui:input name="workPackage"
                        helpMessage="work-package-help"
                        cssClass="timetracker-input" />
                        
                    <aui:input name="titleURL"
                        label="ticket-url" helpMessage="ticket-url-help"
                        cssClass="timetracker-input" />
                        
                    <aui:input name="description" />

                </aui:fieldset>
            </aui:col>

            <aui:col span="6">
                <aui:fieldset>
                
                    <aui:input name="startDate"
                        label="date" />                

                    <c:if test="<%=Validator.equals("from-until", timeFormat)%>">
                        <aui:field-wrapper name="from-until">
                            <iu:time-picker deltaMinutes="<%=15%>"
                                firstHour="<%=0%>"
                                hour="<%=startDateHour%>"
                                minute="<%=startDateMinute%>"
                                nullable="<%=false%>"
                                prefix="<%=startDatePrefix%>" />
                            --
                            <iu:time-picker deltaMinutes="<%=15%>"
                                firstHour="<%=0%>"
                                hour="<%=endDateHour%>"
                                minute="<%=endDateMinute%>"
                                nullable="<%=false%>"
                                prefix="<%=endDatePrefix%>" />

                        </aui:field-wrapper>
                    </c:if>
                    <c:if
                        test="<%=!Validator.equals("from-until", timeFormat)%>">
                        <aui:field-wrapper
                            label="duration"
                            helpMessage="duration-help">
                            <input name="<portlet:namespace/>duration"
                                value="<%=durationInMinutes%>"
                                class="aui-field-input aui-field-input-text lfr-input-text duration-in-minutes" />
                        </aui:field-wrapper>
                    </c:if>

                    <aui:select name="status">
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
                    <aui:button type="submit" />
                </aui:button-row>

            </aui:col>
        </aui:row>
    </aui:form>
</div>

<%-- 
<ifx-util:build-info />
--%>