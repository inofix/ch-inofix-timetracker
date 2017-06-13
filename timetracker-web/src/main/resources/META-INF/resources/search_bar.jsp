<%--
    search.jsp: The extended search of the timetracker portlet.

    Created:     2017-06-05 22:04 by Christian Berndt
    Modified:    2017-06-13 00:55 by Christian Berndt
    Version:     1.0.2
--%>

<%@ include file="/init.jsp" %>

<%
    TaskRecordDisplayTerms displayTerms = new TaskRecordDisplayTerms(renderRequest);
    int status = ParamUtil.getInteger(request, "status");
    
    int fromDateDay = ParamUtil.getInteger(request, "fromDateDay"); 
    int fromDateMonth = ParamUtil.getInteger(request, "fromDateMonth"); 
    int fromDateYear = ParamUtil.getInteger(request, "fromDateYear"); 
    Date fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay, fromDateYear);
    boolean ignoreFromDate = fromDate == null; 
    
    int untilDateDay = ParamUtil.getInteger(request, "untilDateDay"); 
    int untilDateMonth = ParamUtil.getInteger(request, "untilDateMonth"); 
    int untilDateYear = ParamUtil.getInteger(request, "untilDateYear"); 
    Date untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay, untilDateYear);
    boolean ignoreUntilDate = untilDate == null; 
%>

<liferay-ui:search-toggle
    autoFocus="<%=windowState.equals(WindowState.MAXIMIZED)%>"
    buttonLabel="search" displayTerms="<%=displayTerms%>"
    id="toggle_id_task_record_search" markupView="<%=markupView%>">
    
    <aui:fieldset>
        <aui:input inlineField="<%=true%>"
            name="<%=TaskRecordDisplayTerms.WORK_PACKAGE%>" size="20"
            value="<%=displayTerms.getWorkPackage()%>" />
            
        <aui:input inlineField="<%=true%>"
            name="<%=TaskRecordDisplayTerms.DESCRIPTION%>" size="20"
            value="<%=displayTerms.getDescription()%>" />
        
        <aui:input
            dateTogglerCheckboxLabel="ignore-from-date"
            disabled="<%=ignoreFromDate%>" formName="searchFm"
            name="fromDate" model="<%= TaskRecord.class %>"
            inlineField="<%= true %>" />

        <aui:input
            dateTogglerCheckboxLabel="ignore-until-date"
            disabled="<%=ignoreUntilDate%>" formName="searchFm"           
            name="untilDate" model="<%= TaskRecord.class %>"
            inlineField="<%= true %>" />
            
        <aui:input inlineField="<%=true%>"
            name="<%=TaskRecordDisplayTerms.USER_ID%>" size="20"
            value="<%=displayTerms.getDescription()%>" />
            
        <aui:select name="status" inlineField="<%= true %>"
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
