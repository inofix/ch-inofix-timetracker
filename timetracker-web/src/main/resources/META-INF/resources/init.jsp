<%--
    init.jsp: Common setup code for the timetracker portlet.

    Created:     2014-02-01 15:31 by Christian Berndt
    Modified:    2017-04-25 18:09 by Christian Berndt
    Version:     1.0.7 
--%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %>
<%@taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %>
<%@taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>

<%@page import="ch.inofix.timetracker.constants.TaskRecordActionKeys"%>
<%@page import="ch.inofix.timetracker.exception.NoSuchTaskRecordException"%>
<%@page import="ch.inofix.timetracker.model.TaskRecord"%>
<%@page import="ch.inofix.timetracker.service.permission.TaskRecordPermission"%>
<%@page import="ch.inofix.timetracker.service.permission.TimetrackerPortletPermission"%>
<%@page import="ch.inofix.timetracker.service.TaskRecordServiceUtil"%>
<%@page import="ch.inofix.timetracker.web.internal.search.TaskRecordDisplayTerms"%>
<%@page import="ch.inofix.timetracker.web.internal.search.TaskRecordSearch"%>
<%@page import="ch.inofix.timetracker.web.internal.search.TaskRecordSearchTerms"%>
<%@page import="ch.inofix.timetracker.web.internal.constants.TimetrackerWebKeys"%>
<%@page import="ch.inofix.timetracker.web.configuration.TimetrackerConfiguration"%>

<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.model.User"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.search.Document"%>
<%@page import="com.liferay.portal.kernel.search.Hits"%>
<%@page import="com.liferay.portal.kernel.search.Sort"%>
<%@page import="com.liferay.portal.kernel.search.SortFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.security.auth.PrincipalException"%>
<%@page import="com.liferay.portal.kernel.service.UserLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.util.DateUtil"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.util.HttpUtil"%>
<%@page import="com.liferay.portal.kernel.util.KeyValuePair"%>
<%@page import="com.liferay.portal.kernel.util.KeyValuePairComparator"%>
<%@page import="com.liferay.portal.kernel.util.ListUtil"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.util.PortalUtil"%>
<%@page import="com.liferay.portal.kernel.util.SetUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.StringUtil"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.kernel.workflow.WorkflowConstants"%>

<%@page import="java.text.DateFormat"%>
<%@page import="java.text.ParseException"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Date" %>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>

<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="javax.portlet.ResourceURL"%>


<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
    String tabs1 = ParamUtil.getString(request, "tabs1", "browse");
    String tabs2 = ParamUtil.getString(request, "tabs2", "export");

    PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setParameter("tabs1", tabs1); 
    portletURL.setParameter("tabs2", tabs2); 

//     String[] columns = portletPreferences.getValues("columns", new String[] { "task-record-id", "work-package",
//             "start-date", "duration", "create-date", "modified-date", "user-name", "status" });
    
    String currentURL = portletURL.toString();
    
//     String timeFormat = portletPreferences.getValue("time-format", "from-until");
    
    TimetrackerConfiguration timetrackerConfiguration = (TimetrackerConfiguration) renderRequest
            .getAttribute(TimetrackerConfiguration.class.getName());
%>
