<%--
    export_import.jsp: Import taskRecords from an uploaded file. 
    
    Created:    2016-03-21 21:51 by Christian Berndt
    Modified:   2017-06-15 19:08 by Christian Berndt
    Version:    1.1.1
--%>

<%@ include file="/init.jsp"%>

<%
    String redirect = ParamUtil.getString(request, "redirect");

    PortletURL portletURL = renderResponse.createRenderURL();

    portletURL.setParameter("mvcPath", "/view.jsp");
    portletURL.setParameter("redirect", redirect);
    portletURL.setParameter("tabs1", "export-import");

    // TODO: check export-import permissions
%>

<c:choose>
    <c:when test="<%= false %>">
<%--     <c:when test="<%= !GroupPermissionUtil.contains(permissionChecker, themeDisplay.getScopeGroup(), ActionKeys.MANAGE_STAGING) %>"> --%>
        <div class="alert alert-info">
            <liferay-ui:message key="you-do-not-have-permission-to-access-the-requested-resource" />
        </div>
    </c:when>
    <c:otherwise>
        <aui:nav-bar cssClass="navbar-collapse-absolute" markupView="<%= markupView %>">
            <aui:nav cssClass="navbar-nav">

                <%
                    portletURL.setParameter("tabs2", "export");
                %>

                <aui:nav-item
                    href="<%= portletURL.toString() %>"
                    label="export"
                    selected='<%= tabs2.equals("export") %>'
                />

                <%
                    portletURL.setParameter("tabs2", "import");
                %>

                <aui:nav-item
                    href="<%= portletURL.toString() %>"
                    label="import"
                    selected='<%= tabs2.equals("import") %>'
                />
                
                <%
                    portletURL.setParameter("tabs2", "delete");
                %>

                <aui:nav-item
                    href="<%= portletURL.toString() %>"
                    label="delete"
                    selected='<%= tabs2.equals("delete") %>'
                />
            </aui:nav>
        </aui:nav-bar>

        <div class="portlet-export-import-container" id="<portlet:namespace />exportImportPortletContainer">
        
            <liferay-util:include page="/export_import_error.jsp" servletContext="<%= application %>" />

            <c:choose>
                <c:when test='<%= tabs2.equals("export") %>'>
                    <liferay-util:include page="/export/view.jsp" servletContext="<%= application %>" />
                </c:when>
                <c:when test='<%= tabs2.equals("import") %>'>
                    <liferay-util:include page="/import/view.jsp" servletContext="<%= application %>" />
                </c:when>
                <c:when test='<%= tabs2.equals("delete") %>'>
                
                    <portlet:actionURL var="deleteGroupRecordsURL">
                        <portlet:param name="<%= Constants.CMD %>" value="deleteGroupTaskRecords"/>
                        <portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>"/>
                        <portlet:param name="mvcPath" value="/confirmation.jsp"/>
                        <portlet:param name="tabs1" value="export-import"/>
                        <portlet:param name="tabs2" value="delete"/>
                    </portlet:actionURL>
                    
                    <aui:button-row>
                        <liferay-ui:icon-menu>
                            <liferay-ui:icon-delete cssClass="btn btn-danger"  message="delete-group-task-records" url="<%= deleteGroupRecordsURL %>" />
                        </liferay-ui:icon-menu>
                    </aui:button-row>
                    
                    <div>Afterwards run "Reindex all search indexes" from the Server Configuration</div>

                </c:when>                
            </c:choose>
        </div>
    </c:otherwise>
</c:choose>
