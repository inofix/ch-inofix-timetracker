<%--
    export_task_records.jsp: Configure a task_records export.
    
    Created:    2017-05-16 17:30 by Christian Berndt
    Modified:   2017-06-01 18:16 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp" %>

<%@page import="ch.inofix.timetracker.background.task.TaskRecordExportBackgroundTaskExecutor"%>

<%@page import="com.liferay.exportimport.kernel.exception.LARFileNameException"%>
<%@page import="com.liferay.exportimport.kernel.model.ExportImportConfiguration"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil"%>

<%
    long exportImportConfigurationId = 0;

    ExportImportConfiguration exportImportConfiguration = null;
  
    boolean configuredExport = (exportImportConfiguration == null) ? false : true;

    portletDisplay.setShowBackIcon(true);
    portletDisplay.setURLBack(portletURL.toString());

    renderResponse.setTitle(!configuredExport ? LanguageUtil.get(request, "new-custom-export") : LanguageUtil.format(request, "new-export-based-on-x", exportImportConfiguration.getName(), false));
%>

<div class="container-fluid-1280">
    
    <portlet:actionURL name="exportTaskRecords" var="exportTaskRecordsURL">
        <portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>"/>
        <portlet:param name="mvcPath" value="/view.jsp"/>
        <portlet:param name="tabs1" value="export-import" />
        <portlet:param name="tabs2" value="export" />
    </portlet:actionURL>
    
    <aui:form action='<%= exportTaskRecordsURL + "&etag=0&strip=0" %>' cssClass="lfr-export-dialog" method="post" name="fm1">
        <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.EXPORT %>" />
        <aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
        <aui:input name="exportImportConfigurationId" type="hidden" value="<%= String.valueOf(exportImportConfigurationId) %>" />
        
        <liferay-ui:error exception="<%= LARFileNameException.class %>" message="please-enter-a-file-with-a-valid-file-name" />

        <div class="export-dialog-tree">
            <aui:fieldset-group markupView="lexicon">
                <aui:fieldset>
                    <c:choose>
                        <c:when test="<%= exportImportConfiguration == null %>">
                            <aui:input label="title" name="name" placeholder="process-name-placeholder"/>
                        </c:when>
                        <c:otherwise>
                            <aui:input label="title" name="name" value="<%= exportImportConfiguration.getName() %>" />
                        </c:otherwise>
                    </c:choose>
                </aui:fieldset>
            </aui:fieldset-group>
        </div>
        
        <aui:button-row>
            <aui:button cssClass="btn-lg" type="submit" value="export" />

            <aui:button cssClass="btn-lg" href="<%= portletURL.toString() %>" type="cancel" />
        </aui:button-row>
    </aui:form>
</div>
