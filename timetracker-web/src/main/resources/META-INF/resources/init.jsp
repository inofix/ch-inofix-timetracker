<%--
    init.jsp: Common imports and initialization code.

    Created:     2014-02-01 15:31 by Christian Berndt
    Modified:    2017-10-30 21:05 by Christian Berndt
    Version:     1.2.2
--%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend"%>
<%@taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@taglib uri="http://liferay.com/tld/security" prefix="liferay-security"%>
<%@taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>

<%@page import="ch.inofix.timetracker.background.task.TaskRecordExportBackgroundTaskExecutor"%>
<%@page import="ch.inofix.timetracker.background.task.TaskRecordImportBackgroundTaskExecutor"%>
<%@page import="ch.inofix.timetracker.constants.PortletKeys"%>
<%@page import="ch.inofix.timetracker.constants.TaskRecordActionKeys"%>
<%@page import="ch.inofix.timetracker.exception.NoSuchTaskRecordException"%>
<%@page import="ch.inofix.timetracker.model.TaskRecord"%>
<%@page import="ch.inofix.timetracker.service.permission.TaskRecordPermission"%>
<%@page import="ch.inofix.timetracker.service.permission.TimetrackerPortletPermission"%>
<%@page import="ch.inofix.timetracker.service.TaskRecordServiceUtil"%>
<%@page import="ch.inofix.timetracker.service.util.TaskRecordUtil"%>
<%@page import="ch.inofix.timetracker.web.internal.search.TaskRecordDisplayTerms"%>
<%@page import="ch.inofix.timetracker.web.internal.search.TaskRecordSearch"%>
<%@page import="ch.inofix.timetracker.web.internal.search.TaskRecordSearchTerms"%>
<%@page import="ch.inofix.timetracker.web.internal.constants.TimetrackerWebKeys"%>
<%@page import="ch.inofix.timetracker.web.configuration.TimetrackerConfiguration"%>

<%@page import="com.liferay.exportimport.kernel.lar.ExportImportHelper"%>
<%@page import="com.liferay.exportimport.kernel.lar.ExportImportHelperUtil"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTask"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskConstants"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.model.Group"%>
<%@page import="com.liferay.portal.kernel.model.Portlet"%>
<%@page import="com.liferay.portal.kernel.model.User"%>
<%@page import="com.liferay.portal.kernel.portlet.PortalPreferences"%>
<%@page import="com.liferay.portal.kernel.portlet.PortletURLUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.repository.model.FileEntry"%>
<%@page import="com.liferay.portal.kernel.search.Document"%>
<%@page import="com.liferay.portal.kernel.search.Hits"%>
<%@page import="com.liferay.portal.kernel.search.Sort"%>
<%@page import="com.liferay.portal.kernel.search.SortFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.security.auth.PrincipalException"%>
<%@page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil"%>
<%@page import="com.liferay.portal.kernel.service.UserLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.service.PortletLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.util.DateUtil"%>
<%@page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.util.HttpUtil"%>
<%@page import="com.liferay.portal.kernel.util.KeyValuePair"%>
<%@page import="com.liferay.portal.kernel.util.KeyValuePairComparator"%>
<%@page import="com.liferay.portal.kernel.util.ListUtil"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.util.PortalUtil"%>
<%@page import="com.liferay.portal.kernel.util.PrefsPropsUtil"%>
<%@page import="com.liferay.portal.kernel.util.PropsKeys"%>
<%@page import="com.liferay.portal.kernel.util.SetUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.StringUtil"%>
<%@page import="com.liferay.portal.kernel.util.TextFormatter"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.kernel.workflow.WorkflowConstants"%>
<%@page import="com.liferay.trash.kernel.util.TrashUtil"%>

<%@page import="java.text.DateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="java.text.ParseException"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>

<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="javax.portlet.ResourceURL"%>
<%@page import="javax.portlet.WindowState"%>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
    PortalPreferences portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(request);

    String[] columns = portletPreferences.getValues("columns", new String[] {"task-record-id","work-package","from-date","duration","description","user-name","modified-date","status"});
    Format dateFormatDate = FastDateFormatFactoryUtil.getDate(locale, timeZone);
    Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
    String[] exportFileNames = portletPreferences.getValues("exportFileName", new String[] {"out.csv"});
    String[] exportNames = portletPreferences.getValues("exportName", new String[] {"latex"});
    String[] exportScripts = portletPreferences.getValues("exportScript", new String[] {"Enter your freemarker template code."});
    String markupView = portletPreferences.getValue("markupView", "lexicon");
    int maxLength = GetterUtil.getInteger(portletPreferences.getValue("maxLength", "35"));
    boolean showSearchSpeed = GetterUtil.getBoolean(portletPreferences.getValue("showSearchSpeed", "false"));
    String tabs1 = ParamUtil.getString(request, "tabs1", "timetracker");
    String tabs2 = ParamUtil.getString(request, "tabs2", "export");
    String timeFormat = portletPreferences.getValue("timeFormat", "from-until");

    TimetrackerConfiguration timetrackerConfiguration = (TimetrackerConfiguration) request
            .getAttribute(TimetrackerConfiguration.class.getName());

    if (Validator.isNotNull(timetrackerConfiguration)) {

        columns = portletPreferences.getValues("columns", timetrackerConfiguration.columns());
        exportFileNames = portletPreferences.getValues("exportFileName",
                timetrackerConfiguration.exportFileNames());
        exportNames = portletPreferences.getValues("exportName", timetrackerConfiguration.exportNames());
        exportScripts = portletPreferences.getValues("exportScript", timetrackerConfiguration.exportScripts());
        markupView = portletPreferences.getValue("markupView", timetrackerConfiguration.markupView());
        maxLength = GetterUtil.getInteger(portletPreferences.getValue("maxLength", timetrackerConfiguration.maxLength()));
        showSearchSpeed = GetterUtil.getBoolean(portletPreferences.getValue("showSearchSpeed",
                String.valueOf(timetrackerConfiguration.showSearchSpeeed())));
        timeFormat = portletPreferences.getValue("timeFormat", timetrackerConfiguration.timeFormat());

        // because of current checkbox configuration
        if ("false".equals(markupView)) {
            markupView = "";
        }
    }
%>
