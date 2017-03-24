<%--
    import.jspf: Import taskRecords from an uploaded file. 
    
    Created:    2016-03-21 21:51 by Christian Berndt
    Modified:   2017-03-24 23:10 by Christian Berndt
    Version:    1.0.4
--%>

<%@ include file="/init.jsp" %>

<portlet:actionURL var="importXMLURL" name="importXML" />

<portlet:renderURL var="browseURL" />

<%
    boolean hasImportPermission = TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
            TaskRecordActionKeys.IMPORT_TASK_RECORDS); 

    String tabs1 = ParamUtil.getString(request, "tabs1", "import-export");
%>

<aui:form action="<%=importXMLURL%>" enctype="multipart/form-data"
    method="post" name="fm" cssClass="import-form">

    <%
        // TODO: Add error handling
    %>

    <aui:input name="tabs1" value="<%=tabs1%>" type="hidden" />

    <aui:fieldset cssClass="import" label="import">

        <aui:input disabled="<%=!hasImportPermission%>" name="file"
            type="file" inlineField="true" label="" />

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
