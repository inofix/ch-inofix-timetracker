<%--
    import_task_records_resources.jsp: configure the task records import.
    
    Created:    2017-06-01 21:45 by Christian Berndt
    Modified:   2017-06-01 21:45 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp" %>

<%@page import="com.liferay.exportimport.kernel.exception.LARTypeException"%>
<%@page import="com.liferay.exportimport.kernel.exception.LARFileSizeException"%>
<%@page import="com.liferay.exportimport.kernel.exception.LARFileException"%>
<%@page import="com.liferay.exportimport.kernel.lar.ManifestSummary"%>
<%@page import="com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys"%>
<%@page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil"%>

<%
    long groupId = ParamUtil.getLong(request, "groupId");

    Group group = null;

    if (groupId > 0) {
        group = GroupLocalServiceUtil.getGroup(groupId);
    } else {
        group = (Group) request.getAttribute(WebKeys.GROUP);
    }

    FileEntry fileEntry = ExportImportHelperUtil.getTempFileEntry(groupId, themeDisplay.getUserId(),
            ExportImportHelper.TEMP_FOLDER_NAME);

%>

<liferay-ui:error exception="<%= LARFileException.class %>" message="please-specify-a-lar-file-to-import" />

<liferay-ui:error exception="<%= LARFileSizeException.class %>">
    <liferay-ui:message arguments="<%= TextFormatter.formatStorageSize(PrefsPropsUtil.getLong(PropsKeys.UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE), locale) %>" key="please-enter-a-file-with-a-valid-file-size-no-larger-than-x" translateArguments="<%= false %>" />
</liferay-ui:error>

<liferay-ui:error exception="<%= LARTypeException.class %>">

    <%
    LARTypeException lte = (LARTypeException)errorException;
    %>

    <liferay-ui:message arguments="<%= lte.getMessage() %>" key="please-import-a-lar-file-of-the-correct-type-x" />
</liferay-ui:error>

<% // TODO: what else can go wrong? see import_layouts_resources.jsp %>

<portlet:actionURL name="importTaskRecords" var="importTaskRecordsURL">
    <portlet:param name="<%= Constants.CMD %>" value="<%= Constants.IMPORT %>" />
    <portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
</portlet:actionURL>

<aui:form action="<%= importTaskRecordsURL %>" cssClass="lfr-export-dialog" method="post" name="fm1">

    <portlet:renderURL var="portletURL">
        <portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
    </portlet:renderURL>

    <aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
        
    <div class="export-dialog-tree">
        <aui:fieldset-group markupView="lexicon">
            <aui:fieldset cssClass="options-group" label="file-summary">
                <dl class="import-file-details options">
                    <dt>
                        <liferay-ui:message key="name" />
                    </dt>
                    <dd>
                        <%= HtmlUtil.escape(fileEntry.getTitle()) %>
                    </dd>
                    <%-- 
                    <dt>
                        <liferay-ui:message key="export" />
                    </dt>
                    <dd>

                        <%
                        Date exportDate = manifestSummary.getExportDate();
                        %>

                        <span onmouseover="Liferay.Portal.ToolTip.show(this, '<%= HtmlUtil.escapeJS(dateFormatDateTime.format(exportDate)) %>')">
                            <liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - exportDate.getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
                        </span>
                    </dd>
                    --%>
                    <dt>
                        <liferay-ui:message key="author" />
                    </dt>
                    <dd>
                        <%= HtmlUtil.escape(fileEntry.getUserName()) %>
                    </dd>
                    <dt>
                        <liferay-ui:message key="size" />
                    </dt>
                    <dd>
                        <%= TextFormatter.formatStorageSize(fileEntry.getSize(), locale) %>
                    </dd>
                </dl>
            </aui:fieldset>
        </aui:fieldset-group>
        
        <aui:button-row>
            <portlet:renderURL var="backURL">
                <portlet:param name="<%= Constants.CMD %>" value="<%= Constants.VALIDATE %>" />
                <portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
            </portlet:renderURL>

            <aui:button cssClass="btn-lg" href="<%= backURL %>" name="back" value="back" />

            <aui:button cssClass="btn-lg" type="submit" value="import" />
        </aui:button-row>
    </div>
</aui:form>
