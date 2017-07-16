<%--
    view_task_records.jsp: search-container of Inofix' timetracker.
    
    Created:     2017-06-05 13:22 by Christian Berndt
    Modified:    2017-06-25 16:00 by Christian Berndt
    Version:     1.0.8
--%>

<%@ include file="/init.jsp"%>

<%@page import="ch.inofix.timetracker.web.internal.search.EntriesChecker"%>

<%
    TaskRecordSearch searchContainer = (TaskRecordSearch) request.getAttribute("view.jsp-searchContainer");

	EntriesChecker entriesChecker = new EntriesChecker(liferayPortletRequest, liferayPortletResponse);

	searchContainer.setRowChecker(entriesChecker);

	String displayStyle = GetterUtil.getString((String) request.getAttribute("view.jsp-displayStyle"));

	String searchContainerId = ParamUtil.getString(request, "searchContainerId");
%>

<liferay-ui:search-container
    id="taskRecords"
    searchContainer="<%=searchContainer%>"
    var="taskRecordSearchContainer">
    
    <liferay-ui:search-container-row
        className="ch.inofix.timetracker.model.TaskRecord"
        modelVar="taskRecord" keyProperty="taskRecordId">

        <portlet:renderURL var="editURL">
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="taskRecordId"
                value="<%=String.valueOf(taskRecord.getTaskRecordId())%>" />
            <portlet:param name="mvcPath" value="/edit_task_record.jsp" />
        </portlet:renderURL>

        <portlet:renderURL var="viewURL">
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="taskRecordId"
                value="<%=String.valueOf(taskRecord.getTaskRecordId())%>" />
            <portlet:param name="mvcPath" value="/edit_task_record.jsp" />
        </portlet:renderURL>

        <%
			request.setAttribute("editURL", editURL.toString());
			request.setAttribute("viewURL", viewURL.toString());

			boolean hasUpdatePermission = TaskRecordPermission.contains(permissionChecker,
					taskRecord.getTaskRecordId(), TaskRecordActionKeys.UPDATE);
			boolean hasViewPermission = TaskRecordPermission.contains(permissionChecker,
					taskRecord.getTaskRecordId(), TaskRecordActionKeys.VIEW);

			String detailURL = null;

			if (hasUpdatePermission) {
				detailURL = editURL.toString();
			} else if (hasViewPermission) {
				detailURL = viewURL.toString();
			}
        %>

        <%@ include file="/search_columns.jspf"%>

        <liferay-ui:search-container-column-jsp align="right"
            cssClass="entry-action" path="/task_record_action.jsp"
            valign="top" />

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator displayStyle="<%=displayStyle%>"
        markupView="<%=markupView%>" />

</liferay-ui:search-container>

<liferay-ui:message key="sum" />
=
<strong><span id="sum"></span></strong>

<%
    ResourceURL resourceURL = liferayPortletResponse.createResourceURL();

	resourceURL.setResourceID("getSum");

	// Copy render parameters to resourceRequest
	resourceURL.setParameters(renderRequest.getParameterMap());

	resourceURL.setParameter("start", "0");
	resourceURL.setParameter("end", String.valueOf(Integer.MAX_VALUE));
%>

<aui:script use="aui-io-request">
	AUI().ready('aui-io-request', function(A) {
		
		console.log('aui-io-request'); 
		
		A.io.request('<%= resourceURL.toString() %>', {
			on : {
				success : function() {
					var data = this.get('responseData');
					A.one('#sum').setHTML(data);
				}
			}
		});
	});
</aui:script>
