package ch.inofix.timetracker.web.internal.portlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.NoSuchResourceException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.internal.exportimport.configuration.ExportImportTaskRecordsConfigurationSettingsMapFactory;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordService;
import ch.inofix.timetracker.web.configuration.ExportImportConfigurationConstants;
import ch.inofix.timetracker.web.configuration.TimetrackerConfiguration;
import ch.inofix.timetracker.web.internal.constants.TimetrackerWebKeys;

/**
 * View Controller of Inofix' timetracker.
 *
 * @author Christian Berndt
 * @author Stefan Luebbers
 * @created 2013-10-07 10:47
 * @modified 2017-11-10 22:22
 * @version 1.9.3
 */
@Component(
    configurationPid = "ch.inofix.timetracker.web.configuration.TimetrackerConfiguration",
    immediate = true, 
    property = { 
        "com.liferay.portlet.css-class-wrapper=portlet-timetracker",
        "com.liferay.portlet.display-category=category.inofix",
        "com.liferay.portlet.footer-portlet-javascript=/js/main.js",
        "com.liferay.portlet.header-portlet-css=/css/main.css", 
        "com.liferay.portlet.instanceable=false",
        "com.liferay.portlet.scopeable=true",
        "javax.portlet.display-name=Timetracker", 
        "javax.portlet.init-param.template-path=/",
        "javax.portlet.init-param.view-template=/view.jsp",
        "javax.portlet.name=" + PortletKeys.TIMETRACKER,
        "javax.portlet.resource-bundle=content.Language",
        "javax.portlet.security-role-ref=power-user,user" 
    }, 
    service = Portlet.class
)
public class TimetrackerPortlet extends MVCPortlet {

//    @Override
//    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
//            throws IOException, PortletException {
//        
//        _log.info("doView"); 
//
//        renderRequest.setAttribute(TimetrackerConfiguration.class.getName(), _timetrackerConfiguration);
//
//        super.doView(renderRequest, renderResponse);
//    }
    
    
//
//    @Override
//    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
//            throws IOException, PortletException {
//
//        try {
//            getTaskRecord(renderRequest);
//        } catch (Exception e) {
//            if (e instanceof NoSuchResourceException || e instanceof PrincipalException) {
//                SessionErrors.add(renderRequest, e.getClass());
//            } else {
//                throw new PortletException(e);
//            }
//        }
//
//        super.render(renderRequest, renderResponse);
//    }

//    @Activate
//    @Modified
//    protected void activate(Map<Object, Object> properties) {
//
//        _timetrackerConfiguration = ConfigurableUtil.createConfigurable(
//                TimetrackerConfiguration.class, properties);
//        
//    }

//    @Override
//    protected void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse)
//            throws IOException, PortletException {
//
//        if (SessionErrors.contains(renderRequest, PrincipalException.getNestedClasses())
//                || SessionErrors.contains(renderRequest, NoSuchTaskRecordException.class)) {
//            include("/error.jsp", renderRequest, renderResponse);
//        } else {
//            super.doDispatch(renderRequest, renderResponse);
//        }
//    }

    protected void exportTaskRecords(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        long groupId = themeDisplay.getScopeGroupId();
        long userId = themeDisplay.getUserId();

        ExportImportConfiguration exportImportConfiguration = getExportImportConfiguration(actionRequest);

        exportImportConfiguration.setName("TaskRecords");
        exportImportConfiguration.setGroupId(groupId);

        _taskRecordService.exportTaskRecordsAsFileInBackground(userId, exportImportConfiguration);

        Map<String, String[]> parameters = new HashMap<>();

        String mvcPath = ParamUtil.getString(actionRequest, "mvcPath");
        String tabs1 = ParamUtil.getString(actionRequest, "tabs1");
        String tabs2 = ParamUtil.getString(actionRequest, "tabs2");

        parameters.put("mvcPath", new String[] { mvcPath });
        parameters.put("tabs1", new String[] { tabs1 });
        parameters.put("tabs2", new String[] { tabs2 });

        actionResponse.setRenderParameters(parameters);

    }

//    protected String getEditTaskRecordURL(ActionRequest actionRequest, ActionResponse actionResponse,
//            TaskRecord taskRecord) throws Exception {
//
//        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
//
//        String editTaskRecordURL = getRedirect(actionRequest, actionResponse);
//
//        if (Validator.isNull(editTaskRecordURL)) {
//            editTaskRecordURL = PortalUtil.getLayoutFullURL(themeDisplay);
//        }
//
//        String namespace = actionResponse.getNamespace();
//        String windowState = actionResponse.getWindowState().toString();
//
//        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, "p_p_id", PortletKeys.TIMETRACKER);
//        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, "p_p_state", windowState);
//        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "mvcPath",
//                templatePath + "edit_task_record.jsp");
//        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "redirect",
//                getRedirect(actionRequest, actionResponse));
//        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "backURL",
//                ParamUtil.getString(actionRequest, "backURL"));
//        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "taskRecordId",
//                taskRecord.getTaskRecordId());
//
//        return editTaskRecordURL;
//    }

    /**
     * from ExportLayoutsMVCAction
     *
     */
    protected ExportImportConfiguration getExportImportConfiguration(ActionRequest actionRequest) throws Exception {

        Map<String, Serializable> exportTaskRecordsSettingsMap = null;

        long exportImportConfigurationId = ParamUtil.getLong(actionRequest, "exportImportConfigurationId");

        if (exportImportConfigurationId > 0) {
            ExportImportConfiguration exportImportConfiguration = _exportImportConfigurationLocalService
                    .fetchExportImportConfiguration(exportImportConfigurationId);

            if (exportImportConfiguration != null) {
                exportTaskRecordsSettingsMap = exportImportConfiguration.getSettingsMap();
            }
        }

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        if (exportTaskRecordsSettingsMap == null) {

            String fileName = ParamUtil.getString(actionRequest, "exportFileName");

            if (Validator.isNull(fileName)) {
                fileName = LanguageUtil.get(actionRequest.getLocale(), "task-records");
            }

            exportTaskRecordsSettingsMap = ExportImportTaskRecordsConfigurationSettingsMapFactory
                    .buildExportTaskRecordsSettingsMap(themeDisplay.getUserId(), themeDisplay.getPlid(),
                            themeDisplay.getScopeGroupId(), PortletKeys.TIMETRACKER, actionRequest.getParameterMap(),
                            themeDisplay.getLocale(), themeDisplay.getTimeZone(), fileName);
        }

        String taskName = ParamUtil.getString(actionRequest, "name");

        if (Validator.isNull(taskName)) {
            taskName = "TaskRecords";
        }

        return _exportImportConfigurationLocalService.addDraftExportImportConfiguration(themeDisplay.getUserId(),
                taskName, ExportImportConfigurationConstants.TYPE_EXPORT_TASK_RECORDS, exportTaskRecordsSettingsMap);
    }

    /**
     * from com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand
     *
     * @param resourceRequest
     * @return
     */
    protected PortletConfig getPortletConfig(ResourceRequest resourceRequest) {

        String portletId = PortalUtil.getPortletId(resourceRequest);

        return PortletConfigFactoryUtil.get(portletId);
    }

    /**
     * from com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand
     *
     * @param resourceRequest
     * @param path
     * @return
     */
//    protected PortletRequestDispatcher getPortletRequestDispatcher(ResourceRequest resourceRequest, String path) {
//
//        PortletConfig portletConfig = getPortletConfig(resourceRequest);
//
//        PortletContext portletContext = portletConfig.getPortletContext();
//
//        return portletContext.getRequestDispatcher(path);
//    }

    protected void getTaskRecord(PortletRequest portletRequest) throws Exception {

        long taskRecordId = ParamUtil.getLong(portletRequest, "taskRecordId");

        if (taskRecordId <= 0) {
            return;
        }

        TaskRecord taskRecord = _taskRecordService.getTaskRecord(taskRecordId);

        portletRequest.setAttribute(TimetrackerWebKeys.TASK_RECORD, taskRecord);
    }


//    @Reference
//    protected void setDLFileEntryLocalService(DLFileEntryLocalService dlFileEntryLocalService) {
//        this._dlFileEntryLocalService = dlFileEntryLocalService;
//    }

    @Reference(unbind = "-")
    protected void setExportImportConfigurationLocalService(
            ExportImportConfigurationLocalService exportImportConfigurationLocalService) {
        _exportImportConfigurationLocalService = exportImportConfigurationLocalService;
    }

//    @Reference(unbind = "-")
//    protected void setExportImportService(ExportImportService exportImportService) {
//        _exportImportService = exportImportService;
//    }
//
//    @Reference(unbind = "-")
//    protected void setLayoutService(LayoutService layoutService) {
//        _layoutService = layoutService;
//    }

    @Reference(unbind = "-")
    protected void setTaskRecordService(TaskRecordService taskRecordService) {
        this._taskRecordService = taskRecordService;
    }

//    private DLFileEntryLocalService _dlFileEntryLocalService;
    private ExportImportConfigurationLocalService _exportImportConfigurationLocalService;
//    private ExportImportService _exportImportService;
//    private LayoutService _layoutService;
    private TaskRecordService _taskRecordService;

//    private volatile TimetrackerConfiguration _timetrackerConfiguration;

//    private static final String REQUEST_PROCESSED = "request_processed";

    private static final Log _log = LogFactoryUtil.getLog(TimetrackerPortlet.class.getName());

}
