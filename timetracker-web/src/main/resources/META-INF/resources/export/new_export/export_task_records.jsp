<%--
    export_task_records.jsp: Configure a task_records export.
    
    Created:    2017-05-16 17:30 by Christian Berndt
    Modified:   2017-11-17 22:45 by Christian Berndt
    Version:    1.0.6
--%>

<%@ include file="/init.jsp" %>

<%
    Calendar calendar = CalendarFactoryUtil.getCalendar(timeZone, locale);

    int timeZoneOffset = timeZone.getOffset(calendar.getTimeInMillis());
%>

<%
    long exportImportConfigurationId = 0;

    ExportImportConfiguration exportImportConfiguration = null;

    Map<String, Serializable> exportImportConfigurationSettingsMap = Collections.emptyMap();

    if (SessionMessages.contains(liferayPortletRequest,
            portletDisplay.getId() + "exportImportConfigurationId")) {
        exportImportConfigurationId = (Long) SessionMessages.get(liferayPortletRequest,
                portletDisplay.getId() + "exportImportConfigurationId");

        if (exportImportConfigurationId > 0) {
            exportImportConfiguration = ExportImportConfigurationLocalServiceUtil
                    .getExportImportConfiguration(exportImportConfigurationId);
        }

        exportImportConfigurationSettingsMap = (Map<String, Serializable>) SessionMessages
                .get(liferayPortletRequest, portletDisplay.getId() + "settingsMap");
    } else {
        exportImportConfigurationId = ParamUtil.getLong(request, "exportImportConfigurationId");

        if (exportImportConfigurationId > 0) {
            exportImportConfiguration = ExportImportConfigurationLocalServiceUtil
                    .getExportImportConfiguration(exportImportConfigurationId);

            exportImportConfigurationSettingsMap = exportImportConfiguration.getSettingsMap();
        }
    }

    boolean configuredExport = (exportImportConfiguration == null) ? false : true;

    String redirect = ParamUtil.getString(request, "redirect");

    String displayStyle = ParamUtil.getString(request, "displayStyle");

    portletDisplay.setShowBackIcon(true);
    portletDisplay.setURLBack(redirect);

    renderResponse.setTitle(!configuredExport ? LanguageUtil.get(request, "new-custom-export")
            : LanguageUtil.format(request, "new-export-based-on-x", exportImportConfiguration.getName(),
                    false));
%>

<div class="container-fluid-1280">
    <portlet:actionURL name="exportTaskRecords" var="restoreTrashEntriesURL">
<%--     <portlet:actionURL name="editExportConfiguration" var="restoreTrashEntriesURL"> --%>
        <portlet:param name="mvcRenderCommandName" value="exportTaskRecords" />
        <portlet:param name="<%= Constants.CMD %>" value="<%= Constants.RESTORE %>" />
    </portlet:actionURL>

    <liferay-trash:undo
        portletURL="<%= restoreTrashEntriesURL %>"
    />

    <%
        int incompleteBackgroundTaskCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(scopeGroupId, TaskRecordExportBackgroundTaskExecutor.class.getName(), false);
    %>

    <div class="<%= (incompleteBackgroundTaskCount == 0) ? "hide" : "in-progress" %>" id="<portlet:namespace />incompleteProcessMessage">
        <liferay-util:include page="/incomplete_processes_message.jsp" servletContext="<%= application %>">
            <liferay-util:param name="incompleteBackgroundTaskCount" value="<%= String.valueOf(incompleteBackgroundTaskCount) %>" />
        </liferay-util:include>
    </div>

    <portlet:actionURL name="exportTaskRecords" var="exportTaskRecordsURL">
        <portlet:param name="mvcRenderCommandName" value="exportTaskRecords" />
        <portlet:param name="redirect" value="<%= redirect %>"/>
    </portlet:actionURL>

    <aui:form action='<%= exportTaskRecordsURL + "&etag=0&strip=0" %>' cssClass="lfr-export-dialog" method="post" name="fm1">
        <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.EXPORT %>" />
        <aui:input name="redirect" type="hidden" value="<%= redirect %>" />
        <aui:input name="exportImportConfigurationId" type="hidden" value="<%= String.valueOf(exportImportConfigurationId) %>" />
        <aui:input name="groupId" type="hidden" value="<%= String.valueOf(scopeGroupId) %>" />

        <liferay-ui:error exception="<%= LARFileNameException.class %>" message="please-enter-a-file-with-a-valid-file-name" />

        <div class="export-dialog-tree">
            <aui:fieldset-group markupView="lexicon">
                <aui:fieldset>
                    <c:choose>
                        <c:when test="<%= exportImportConfiguration == null %>">
                            <aui:input label="title" name="name" placeholder="process-name-placeholder" />
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
            <aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
        </aui:button-row>
    </aui:form>
</div>

<aui:script use="liferay-export-import">
    var exportImport = new Liferay.ExportImport(
        {
            exportLAR: true,
            form: document.<portlet:namespace />fm1,
            incompleteProcessMessageNode: '#<portlet:namespace />incompleteProcessMessage',
            locale: '<%= locale.toLanguageTag() %>',
            namespace: '<portlet:namespace />',
            rangeAllNode: '#rangeAll',
            rangeDateRangeNode: '#rangeDateRange',
            rangeLastNode: '#rangeLast',
            timeZoneOffset: <%= timeZoneOffset %>
        }
    );

    Liferay.component('<portlet:namespace />ExportImportComponent', exportImport);

    var form = A.one('#<portlet:namespace />fm1');

    form.on(
        'submit',
        function(event) {
            event.halt();

            var exportImport = Liferay.component('<portlet:namespace />ExportImportComponent');
            
            // console.log(exportImport); 

            submitForm(form, form.attr('action'), false);

        }
    );
</aui:script>
