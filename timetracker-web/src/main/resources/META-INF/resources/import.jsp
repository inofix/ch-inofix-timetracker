<%--
    import.jspf: Import taskRecords from an uploaded file. 
    
    Created:    2016-03-21 21:51 by Christian Berndt
    Modified:   2017-04-08 22:33 by Christian Berndt
    Version:    1.0.5
--%>

<%@ include file="/init.jsp"%>

<%
long groupId = scopeGroupId; 
// long groupId = ParamUtil.getLong(request, "groupId");

    boolean hasImportPermission = TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
            TaskRecordActionKeys.IMPORT_TASK_RECORDS);

    boolean privateLayout = ParamUtil.getBoolean(request, "privateLayout");

    String tabs1 = ParamUtil.getString(request, "tabs1", "import-export");
%>

<%-- <portlet:actionURL var="importDataURL" name="importData" /> --%>
<%-- <portlet:actionURL var="importXMLURL" name="importXML" /> --%>

<liferay-portlet:actionURL doAsUserId="<%=user.getUserId()%>"
    name="processAction" var="actionURL">
    <portlet:param name="mvcRenderCommandName" value="importLayouts" />
    <portlet:param name="<%=Constants.CMD%>"
        value="<%=Constants.ADD_TEMP%>" />
<%--     <portlet:param name="groupId" value="<%=String.valueOf(groupId)%>" /> --%>
    <portlet:param name="privateLayout"
        value="<%=String.valueOf(privateLayout)%>" />
</liferay-portlet:actionURL>

<liferay-portlet:actionURL doAsUserId="<%=user.getUserId()%>"
    name="processAction" var="importDataURL">
    <portlet:param name="mvcRenderCommandName" value="importLayouts" />
    <portlet:param name="<%=Constants.CMD%>"
        value="<%=Constants.IMPORT%>" />
    <portlet:param name="groupId" value="<%=String.valueOf(groupId)%>" />
    <portlet:param name="privateLayout"
        value="<%=String.valueOf(privateLayout)%>" />

    <portlet:param name="redirect" value="/view.jsp" />
</liferay-portlet:actionURL>

<portlet:renderURL var="browseURL" />

<aui:form action="<%=actionURL%>" enctype="multipart/form-data"
    method="post" name="fm" cssClass="import-form">

    <%
        // TODO: Add error handling
    %>

    <aui:input name="tabs1" value="<%=tabs1%>" type="hidden" />

    <aui:fieldset cssClass="import" label="import">

        <aui:input disabled="<%=!hasImportPermission%>" name="file"
            type="file" inlineField="true" label="" />
            
        <aui:input name="groupId" type="hidden" value="<%= String.valueOf(groupId)%>" />

        <%-- 
            <aui:input name="updateExisting" label="update-existing-task-records" type="checkbox" inlineField="true" />
        --%>

        <aui:button-row>
            <aui:button name="import" type="submit" value="import"
                disabled="true" />
            <aui:button href="<%=browseURL%>" type="cancel" />
        </aui:button-row>

    </aui:fieldset>

</aui:form>


<aui:a href="<%=importDataURL%>" cssClass="btn btn-primary"
    label="import" />


<aui:script use="aui-base">
	var input = A.one('#<portlet:namespace />file');
	var button = A.one('#<portlet:namespace />import');

	input.on('change', function(e) {

		if (input.get('value')) {
			button.removeClass('disabled');
			button.removeAttribute('disabled');
		} else {
			button.addClass('disabled');
			button.setAttrs({
				disabled : 'disabled'
			});
		}

	});
</aui:script>
	



