<%--
    export_task_records.jsp: Configure a task_records export.
    
    Created:    2017-05-16 17:30 by Christian Berndt
    Modified:   2017-05-16 17:30 by Christian Berndt
    Version:    1.0.0
--%>



<%@ include file="/init.jsp" %>

<%@page import="java.io.Serializable"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Collections"%>

<%@page import="ch.inofix.timetracker.background.task.TaskRecordExportBackgroundTaskExecutor"%>
<%@page import="ch.inofix.timetracker.model.impl.TaskRecordBaseImpl"%>

<%@page import="com.liferay.exportimport.kernel.exception.LARFileNameException"%>
<%@page import="com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys"%>
<%@page import="com.liferay.exportimport.kernel.model.ExportImportConfiguration"%>
<%@page import="com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil"%>
<%@page import="com.liferay.portal.kernel.servlet.SessionMessages"%>

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

//     String rootNodeName = StringPool.BLANK;

    if (configuredExport) {
        // privateLayout = MapUtil.getBoolean(exportImportConfigurationSettingsMap, "privateLayout", privateLayout);
    }

    String displayStyle = ParamUtil.getString(request, "displayStyle");

//     PortletURL portletURL = renderResponse.createRenderURL();

    portletURL.setParameter("mvcRenderCommandName", "exportLayoutsView");
    portletURL.setParameter("groupId", String.valueOf(scopeGroupId));
    portletURL.setParameter("displayStyle", displayStyle);

    portletDisplay.setShowBackIcon(true);
    portletDisplay.setURLBack(portletURL.toString());

    renderResponse.setTitle(!configuredExport ? LanguageUtil.get(request, "new-custom-export") : LanguageUtil.format(request, "new-export-based-on-x", exportImportConfiguration.getName(), false));

%>

<h3>export_task_records.jsp </h3>

exportImportConfigurationId = <%= exportImportConfigurationId %>


<div class="container-fluid-1280">
    <%
        int incompleteBackgroundTaskCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(scopeGroupId, TaskRecordExportBackgroundTaskExecutor.class.getName(), false);
    %>

    incompleteBackgroundTaskCount = <%= incompleteBackgroundTaskCount %>
    
    <div class="<%= (incompleteBackgroundTaskCount == 0) ? "hide" : "in-progress" %>" id="<portlet:namespace />incompleteProcessMessage">
        <liferay-util:include page="/incomplete_processes_message.jsp" servletContext="<%= application %>">
            <liferay-util:param name="incompleteBackgroundTaskCount" value="<%= String.valueOf(incompleteBackgroundTaskCount) %>" />
        </liferay-util:include>
    </div>
    
    <portlet:actionURL name="exportTaskRecords" var="exportTaskRecordsURL">
        <portlet:param name="exportLAR" value="<%= Boolean.TRUE.toString() %>" />
        <portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>"/>
        <portlet:param name="mvcPath" value="/export/view.jsp"/>
        <portlet:param name="tabs1" value="export-import" />
        <portlet:param name="tabs2" value="export" />
    </portlet:actionURL>
    
<%--     <portlet:actionURL name="exportLayouts" var="exportPagesURL"> --%>
<%--         <portlet:param name="mvcRenderCommandName" value="exportLayouts" /> --%>
<%--         <portlet:param name="exportLAR" value="<%= Boolean.TRUE.toString() %>" /> --%>
<%--     </portlet:actionURL> --%>

    <aui:form action='<%= exportTaskRecordsURL + "&etag=0&strip=0" %>' cssClass="lfr-export-dialog" method="post" name="fm1">
        <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.EXPORT %>" />
        <aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
        <aui:input name="exportImportConfigurationId" type="hidden" value="<%= String.valueOf(exportImportConfigurationId) %>" />
        <aui:input name="groupId" type="hidden" value="<%= String.valueOf(scopeGroupId) %>" />
<%--         <aui:input name="liveGroupId" type="hidden" value="<%= String.valueOf(liveGroupId) %>" /> --%>
<%--         <aui:input name="privateLayout" type="hidden" value="<%= String.valueOf(privateLayout) %>" /> --%>
<%--         <aui:input name="rootNodeName" type="hidden" value="<%= rootNodeName %>" /> --%>
<%--         <aui:input name="treeId" type="hidden" value="<%= treeId %>" /> --%>
        <aui:input name="<%= PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL %>" type="hidden" value="<%= true %>" />
        <aui:input name="<%= PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL %>" type="hidden" value="<%= true %>" />
        <aui:input name="<%= PortletDataHandlerKeys.PORTLET_SETUP_ALL %>" type="hidden" value="<%= true %>" />
        <aui:input name="<%= PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL %>" type="hidden" value="<%= true %>" />

        <liferay-ui:error exception="<%= LARFileNameException.class %>" message="please-enter-a-file-with-a-valid-file-name" />

        <div class="export-dialog-tree">
            <aui:fieldset-group markupView="lexicon">
                <aui:fieldset>
                    <c:choose>
                        <c:when test="<%= exportImportConfiguration == null %>">
                            <aui:input label="title" name="name" placeholder="process-name-placeholder"  required="<%= true %>"/>
                        </c:when>
                        <c:otherwise>
                            <aui:input label="title" name="name" value="<%= exportImportConfiguration.getName() %>" />
                        </c:otherwise>
                    </c:choose>
                </aui:fieldset>

<%--                 <c:if test="<%= !group.isLayoutPrototype() && !group.isCompany() %>"> --%>
<%--                     <liferay-staging:select-pages action="<%= Constants.EXPORT %>" disableInputs="<%= configuredExport %>" exportImportConfigurationId="<%= exportImportConfigurationId %>" groupId="<%= liveGroupId %>" privateLayout="<%= privateLayout %>" treeId="<%= treeId %>" /> --%>
<%--                 </c:if> --%>

                <liferay-staging:content cmd="<%= Constants.EXPORT %>" disableInputs="<%= configuredExport %>" exportImportConfigurationId="<%= exportImportConfigurationId %>" type="<%= Constants.EXPORT %>" />

                <liferay-staging:deletions cmd="<%= Constants.EXPORT %>" exportImportConfigurationId="<%= exportImportConfigurationId %>" />

<%--                 <liferay-staging:permissions action="<%= Constants.EXPORT %>" descriptionCSSClass="permissions-description" disableInputs="<%= configuredExport %>" exportImportConfigurationId="<%= exportImportConfigurationId %>" global="<%= group.isCompany() %>" labelCSSClass="permissions-label" /> --%>
            </aui:fieldset-group>
        </div>

        <aui:button-row>
            <aui:button cssClass="btn-lg" type="submit" value="export" />

            <aui:button cssClass="btn-lg" href="<%= portletURL.toString() %>" type="cancel" />
        </aui:button-row>
    </aui:form>
</div>

<aui:script use="liferay-export-import">
    new Liferay.ExportImport(
        {
            archivedSetupsNode: '#<%= PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL %>',
            commentsNode: '#<%= PortletDataHandlerKeys.COMMENTS %>',
            deletionsNode: '#<%= PortletDataHandlerKeys.DELETIONS %>',
            exportLAR: true,
            form: document.<portlet:namespace />fm1,
            incompleteProcessMessageNode: '#<portlet:namespace />incompleteProcessMessage',
            locale: '<%= locale.toLanguageTag() %>',
            namespace: '<portlet:namespace />',
            rangeAllNode: '#rangeAll',
            rangeDateRangeNode: '#rangeDateRange',
            rangeLastNode: '#rangeLast',
            ratingsNode: '#<%= PortletDataHandlerKeys.RATINGS %>',
            setupNode: '#<%= PortletDataHandlerKeys.PORTLET_SETUP_ALL %>',
            timeZone: '<%= timeZone.getID() %>',
            userPreferencesNode: '#<%= PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL %>'
        }
    );

    var form = A.one('#<portlet:namespace />fm1');

    form.on(
        'submit',
        function(event) {
            event.preventDefault();

            var A = AUI();

            var allContentSelected = A.one('#<portlet:namespace /><%= PortletDataHandlerKeys.PORTLET_DATA_ALL %>').val();

            if (allContentSelected === 'true') {
                var portletDataControlDefault = A.one('#<portlet:namespace /><%= PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT %>');

                portletDataControlDefault.val(true);
            }

            submitForm(form, form.attr('action'), false);
        }
    );
</aui:script>

<aui:script>
    Liferay.Util.toggleRadio('<portlet:namespace />chooseApplications', '<portlet:namespace />selectApplications', ['<portlet:namespace />showChangeGlobalConfiguration']);
    Liferay.Util.toggleRadio('<portlet:namespace />allApplications', '<portlet:namespace />showChangeGlobalConfiguration', ['<portlet:namespace />selectApplications']);

    Liferay.Util.toggleRadio('<portlet:namespace />rangeAll', '', ['<portlet:namespace />startEndDate', '<portlet:namespace />rangeLastInputs']);
    Liferay.Util.toggleRadio('<portlet:namespace />rangeDateRange', '<portlet:namespace />startEndDate', '<portlet:namespace />rangeLastInputs');
    Liferay.Util.toggleRadio('<portlet:namespace />rangeLast', '<portlet:namespace />rangeLastInputs', ['<portlet:namespace />startEndDate']);
</aui:script>
