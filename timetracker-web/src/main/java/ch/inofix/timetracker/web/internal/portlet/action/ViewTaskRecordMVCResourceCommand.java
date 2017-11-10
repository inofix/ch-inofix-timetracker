package ch.inofix.timetracker.web.internal.portlet.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordServiceUtil;
import ch.inofix.timetracker.service.util.TimetrackerUtil;
import ch.inofix.timetracker.web.configuration.TimetrackerConfiguration;
import ch.inofix.timetracker.web.internal.portlet.util.TemplateUtil;
import ch.inofix.timetracker.web.internal.search.TaskRecordSearch;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-10 16:36
 * @modified 2017-11-10 16:36
 * @version 1.0.0
 *
 */
@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + PortletKeys.TIMETRACKER,
        "mvc.command.name=viewTaskRecord"
    },
    service = MVCResourceCommand.class
)
public class ViewTaskRecordMVCResourceCommand extends BaseMVCResourceCommand {
    
    @Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {

        _timetrackerConfiguration = ConfigurableUtil.createConfigurable(
                TimetrackerConfiguration.class, properties);
        
    }

    @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
            throws Exception {

        _log.info("doServeResource()");

        String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);

        _log.info("cmd = " + cmd);

        PortletRequestDispatcher portletRequestDispatcher = null;

        if (cmd.equals("download")) {

            download(resourceRequest, resourceResponse);

        } else if (cmd.equals("getSum")) {

            getSum(resourceRequest, resourceResponse);

        } else {
            
            portletRequestDispatcher = getPortletRequestDispatcher(resourceRequest, "/view.jsp");

            portletRequestDispatcher.include(resourceRequest, resourceResponse);

        }

    }
    
    protected void download(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws Exception {
        
        int idx = ParamUtil.getInteger(resourceRequest, "idx"); 
        
        _log.info("idx = " + idx);

        PortletPreferences portletPreferences = resourceRequest.getPreferences();

        List<TaskRecord> taskRecords = getTaskRecords(resourceRequest);

        Map<String, Object> contextObjects = new HashMap<>();

        contextObjects.put("taskRecords", taskRecords);

        String[] exportFileNames = portletPreferences.getValues("exportFileName",
                _timetrackerConfiguration.exportFileNames());
        String[] exportNames = portletPreferences.getValues("exportName", _timetrackerConfiguration.exportNames());
        String[] exportScripts = portletPreferences.getValues("exportScript",
                _timetrackerConfiguration.exportScripts());

        String exportStr = null;

        try {
            exportStr = TemplateUtil.transform(contextObjects, exportScripts[idx], exportNames[idx], "ftl");
        } catch (Exception e) {
            exportStr = e.getCause().getMessage();
        }

        PortletResponseUtil.sendFile(resourceRequest, resourceResponse, exportFileNames[idx], exportStr.getBytes());

    }
    
    protected void getSum(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws Exception {

        List<TaskRecord> taskRecords = getTaskRecords(resourceRequest);

        long minutes = 0;

        for (TaskRecord taskRecord : taskRecords) {
            minutes = minutes + taskRecord.getDurationInMinutes();
        }

        double hours = 0;

        if (minutes > 0) {
            hours = ((double) minutes) / 60;
        }

        PortletResponseUtil.write(resourceResponse, String.valueOf(hours));

    }
    
    protected List<TaskRecord> getTaskRecords(PortletRequest request) throws Exception {
        
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        PortletURL iteratorURL = PortletURLFactoryUtil.create(request, PortletKeys.TIMETRACKER,
                themeDisplay.getLayout(), PortletRequest.RENDER_PHASE);

        String description = ParamUtil.getString(request, "description");
        boolean advancedSearch = ParamUtil.getBoolean(request, "advancedSearch", false);
        boolean andOperator = ParamUtil.getBoolean(request, "andOperator", true);
        int end = ParamUtil.getInteger(request, "end");

        boolean ignoreFromDate = ParamUtil.getBoolean(request, "ignoreFromDate");

        Date fromDate = null;

        if (!ignoreFromDate) {

            int fromDateDay = ParamUtil.getInteger(request, "fromDateDay");
            int fromDateMonth = ParamUtil.getInteger(request, "fromDateMonth");
            int fromDateYear = ParamUtil.getInteger(request, "fromDateYear");
            fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay, fromDateYear);
        }

        String keywords = ParamUtil.getString(request, "keywords");
        String orderByCol = ParamUtil.getString(request, "orderByCol", "modifiedDate");
        String orderByType = ParamUtil.getString(request, "orderByType", "desc");
        long ownerUserId = ParamUtil.getLong(request, "ownerUserId");
        int start = ParamUtil.getInteger(request, "start");
        int status = ParamUtil.getInteger(request, Field.STATUS);

        TaskRecordSearch taskRecordSearch = new TaskRecordSearch(request, iteratorURL);

        orderByCol = taskRecordSearch.getOrderByCol();

        boolean ignoreUntilDate = ParamUtil.getBoolean(request, "ignoreUntilDate");

        Date untilDate = null;

        if (!ignoreUntilDate) {

            int untilDateDay = ParamUtil.getInteger(request, "untilDateDay");
            int untilDateMonth = ParamUtil.getInteger(request, "untilDateMonth");
            int untilDateYear = ParamUtil.getInteger(request, "untilDateYear");
            untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay, untilDateYear);
        }

        String workPackage = ParamUtil.getString(request, "workPackage");

        boolean reverse = "desc".equals(orderByType);

        Sort sort = new Sort(orderByCol, reverse);

        Hits hits = null;

        if (advancedSearch) {
            hits = TaskRecordServiceUtil.search(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), ownerUserId,
                    workPackage, description, status, fromDate, untilDate, null, andOperator, advancedSearch, start,
                    end, sort);
        } else {
            hits = TaskRecordServiceUtil.search(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), 0, keywords,
                    start, end, sort);
        }

        List<TaskRecord> taskRecords = TimetrackerUtil.getTaskRecords(hits);

        return taskRecords;

    }
    
    private volatile TimetrackerConfiguration _timetrackerConfiguration;
    
    private static Log _log = LogFactoryUtil.getLog(ViewTaskRecordMVCResourceCommand.class.getName());

}
