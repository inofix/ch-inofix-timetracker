<%--
    add_button.jsp: add a task-record 
    
    Created:    2017-06-05 14:21 by Christian Berndt
    Modified:   2017-11-18 13:34 by Christian Berndt
    Version:    1.0.5
--%>

<%@ include file="/init.jsp" %>

<c:if test="<%=TimetrackerPortletPermission.contains(permissionChecker, scopeGroupId,
                    TimetrackerActionKeys.ADD_TASK_RECORD)%>">

    <liferay-frontend:add-menu>

        <portlet:renderURL var="addTaskRecordURL">
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="mvcPath" value="/edit_task_record.jsp" />
        </portlet:renderURL>

        <liferay-frontend:add-menu-item
            title='<%=LanguageUtil.get(request, "add-task-record")%>'
            url="<%=addTaskRecordURL.toString()%>" />

    </liferay-frontend:add-menu>

</c:if>
