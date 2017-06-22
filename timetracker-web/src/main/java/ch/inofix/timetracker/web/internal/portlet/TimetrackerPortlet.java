package ch.inofix.timetracker.web.internal.portlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.exception.LARFileException;
import com.liferay.exportimport.kernel.exception.LARFileSizeException;
import com.liferay.exportimport.kernel.exception.LARTypeException;
import com.liferay.exportimport.kernel.exception.LayoutImportException;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.MissingReference;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportService;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.exception.LayoutPrototypeException;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchBackgroundTaskException;
import com.liferay.portal.kernel.exception.NoSuchResourceException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import aQute.bnd.annotation.metatype.Configurable;
import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.exception.NoSuchTaskRecordException;
import ch.inofix.timetracker.exception.TaskRecordFromDateException;
import ch.inofix.timetracker.exception.TaskRecordUntilDateException;
import ch.inofix.timetracker.internal.exportimport.configuration.ExportImportTaskRecordsConfigurationSettingsMapFactory;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordService;
import ch.inofix.timetracker.service.TaskRecordServiceUtil;
import ch.inofix.timetracker.service.util.TaskRecordUtil;
import ch.inofix.timetracker.web.configuration.ExportImportConfigurationConstants;
import ch.inofix.timetracker.web.configuration.TimetrackerConfiguration;
import ch.inofix.timetracker.web.internal.constants.TimetrackerWebKeys;
import ch.inofix.timetracker.web.internal.portlet.util.PortletUtil;
import ch.inofix.timetracker.web.internal.portlet.util.TemplateUtil;

/**
 * View Controller of Inofix' timetracker.
 *
 * @author Christian Berndt
 * @author Stefan Luebbers
 * @created 2013-10-07 10:47
 * @modified 2017-06-22 21:12
 * @version 1.8.1
 */
@Component(immediate = true, property = { "com.liferay.portlet.css-class-wrapper=portlet-timetracker",
        "com.liferay.portlet.display-category=category.inofix",
        "com.liferay.portlet.footer-portlet-javascript=/js/main.js",
        "com.liferay.portlet.header-portlet-css=/css/main.css", "com.liferay.portlet.instanceable=false",
        "javax.portlet.display-name=Timetracker", "javax.portlet.init-param.template-path=/",
        "javax.portlet.init-param.view-template=/view.jsp", "javax.portlet.resource-bundle=content.Language",
        "javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class TimetrackerPortlet extends MVCPortlet {

    @Override
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        renderRequest.setAttribute(TimetrackerConfiguration.class.getName(), _timetrackerConfiguration);

        super.doView(renderRequest, renderResponse);
    }

    /**
     * From ImportLayoutsMVCCommand
     *
     * @param actionRequest
     * @param actionResponse
     * @throws Exception
     */
    @Override
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

        _log.info("processAction");
        _log.info("cmd = " + cmd);

        try {
            if (cmd.equals(Constants.ADD_TEMP)) {
                addTempFileEntry(actionRequest, ExportImportHelper.TEMP_FOLDER_NAME);

                validateFile(actionRequest, actionResponse, ExportImportHelper.TEMP_FOLDER_NAME);
                hideDefaultSuccessMessage(actionRequest);

            } else if (cmd.equals(Constants.DELETE)) {

                deleteTaskRecords(actionRequest, actionResponse);
                addSuccessMessage(actionRequest, actionResponse);

            } else if (cmd.equals("deleteBackgroundTasks")) {

                deleteBackgroundTasks(actionRequest, actionResponse);
                addSuccessMessage(actionRequest, actionResponse);

            } else if (cmd.equals("deleteGroupTaskRecords")) {

                deleteGroupTaskRecords(actionRequest, actionResponse);

            } else if (cmd.equals(Constants.DELETE_TEMP)) {

                deleteTempFileEntry(actionRequest, actionResponse, ExportImportHelper.TEMP_FOLDER_NAME);
                hideDefaultSuccessMessage(actionRequest);

            } else if (cmd.equals(Constants.EXPORT)) {

                exportTaskRecords(actionRequest, actionResponse);
                hideDefaultSuccessMessage(actionRequest);

            } else if (cmd.equals(Constants.IMPORT)) {

                hideDefaultSuccessMessage(actionRequest);
                importTaskRecords(actionRequest, ExportImportHelper.TEMP_FOLDER_NAME);

                Map<String, String[]> parameters = new HashMap<>();

                String mvcPath = ParamUtil.getString(actionRequest, "mvcPath");
                String tabs1 = ParamUtil.getString(actionRequest, "tabs1");
                String tabs2 = ParamUtil.getString(actionRequest, "tabs2");

                parameters.put("mvcPath", new String[] { mvcPath });
                parameters.put("tabs1", new String[] { tabs1 });
                parameters.put("tabs2", new String[] { tabs2 });

                actionResponse.setRenderParameters(parameters);

            } else if (cmd.equals(Constants.UPDATE)) {

                updateTaskRecord(actionRequest, actionResponse);
                addSuccessMessage(actionRequest, actionResponse);

            }
        } catch (Exception e) {

            if (cmd.equals(Constants.ADD_TEMP) || cmd.equals(Constants.DELETE_TEMP)) {

                hideDefaultSuccessMessage(actionRequest);

                // TODO
                // handleUploadException(actionRequest, actionResponse,
                // ExportImportHelper.TEMP_FOLDER_NAME, e);

            } else {
                if ((e instanceof LARFileException) || (e instanceof LARFileSizeException)
                        || (e instanceof LARTypeException)) {

                    SessionErrors.add(actionRequest, e.getClass());
                } else if ((e instanceof LayoutPrototypeException) || (e instanceof LocaleException)) {

                    SessionErrors.add(actionRequest, e.getClass(), e);
                } else {
                    _log.error(e, e);

                    SessionErrors.add(actionRequest, LayoutImportException.class.getName());
                }
            }
        }
    }

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        try {
            getTaskRecord(renderRequest);
        } catch (Exception e) {
            if (e instanceof NoSuchResourceException || e instanceof PrincipalException) {
                SessionErrors.add(renderRequest, e.getClass());
            } else {
                throw new PortletException(e);
            }
        }

        super.render(renderRequest, renderResponse);
    }

    @Override
    public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
            throws PortletException {

        try {
            String resourceID = resourceRequest.getResourceID();

            if (resourceID.equals("download")) {
                download(resourceRequest, resourceResponse);
            } else if (resourceID.equals("getSum")) {
                getSum(resourceRequest, resourceResponse);
            } else if (resourceID.equals("importTaskRecords")) {
                importTaskRecords(resourceRequest, resourceResponse);
            } else {
                super.serveResource(resourceRequest, resourceResponse);
            }
        } catch (Exception e) {
            throw new PortletException(e);
        }
    }

    @Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {
        _timetrackerConfiguration = Configurable.createConfigurable(TimetrackerConfiguration.class, properties);
    }

    protected void addTempFileEntry(ActionRequest actionRequest, String folderName) throws Exception {

        UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(actionRequest);

        checkExceededSizeLimit(uploadPortletRequest);

        long groupId = ParamUtil.getLong(actionRequest, "groupId");

        deleteTempFileEntry(groupId, folderName);

        InputStream inputStream = null;

        try {
            String sourceFileName = uploadPortletRequest.getFileName("file");

            inputStream = uploadPortletRequest.getFileAsStream("file");

            String contentType = uploadPortletRequest.getContentType("file");

            _layoutService.addTempFileEntry(groupId, folderName, sourceFileName, inputStream, contentType);
        } catch (Exception e) {
            UploadException uploadException = (UploadException) actionRequest.getAttribute(WebKeys.UPLOAD_EXCEPTION);

            if (uploadException != null) {
                Throwable cause = uploadException.getCause();

                // TODO
                // if (cause instanceof FileUploadBase.IOFileUploadException) {
                // if (_log.isInfoEnabled()) {
                // _log.info("Temporary upload was cancelled");
                // }
                // }

                if (uploadException.isExceededFileSizeLimit()) {
                    throw new FileSizeException(cause);
                }

                if (uploadException.isExceededUploadRequestSizeLimit()) {
                    throw new UploadRequestSizeException(cause);
                }
            } else {
                throw e;
            }
        } finally {
            StreamUtil.cleanUp(inputStream);
        }
    }

    protected void checkExceededSizeLimit(HttpServletRequest request) throws PortalException {

        UploadException uploadException = (UploadException) request.getAttribute(WebKeys.UPLOAD_EXCEPTION);

        if (uploadException != null) {
            Throwable cause = uploadException.getCause();

            if (uploadException.isExceededFileSizeLimit() || uploadException.isExceededUploadRequestSizeLimit()) {

                throw new LARFileSizeException(cause);
            }

            throw new PortalException(cause);
        }
    }

    protected void deleteBackgroundTasks(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        _log.info("deleteBackgroundTasks");

        try {
            long[] backgroundTaskIds = ParamUtil.getLongValues(actionRequest, "deleteBackgroundTaskIds");

            for (long backgroundTaskId : backgroundTaskIds) {
                BackgroundTaskManagerUtil.deleteBackgroundTask(backgroundTaskId);
            }
        } catch (Exception e) {
            if (e instanceof NoSuchBackgroundTaskException || e instanceof PrincipalException) {

                SessionErrors.add(actionRequest, e.getClass());

                actionResponse.setRenderParameter("mvcPath", "/error.jsp");
            } else {
                throw e;
            }
        }

        String tabs1 = ParamUtil.getString(actionRequest, "tabs1");
        String tabs2 = ParamUtil.getString(actionRequest, "tabs2");

        actionResponse.setRenderParameter("tabs1", tabs1);
        actionResponse.setRenderParameter("tabs2", tabs2);
    }

    /**
     * @param actionRequest
     * @param actionResponse
     * @since 1.0.8
     * @throws Exception
     */
    protected void deleteGroupTaskRecords(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        ServiceContext serviceContext = ServiceContextFactory.getInstance(TaskRecord.class.getName(), actionRequest);

        List<TaskRecord> taskRecords = _taskRecordService.deleteGroupTaskRecords(serviceContext.getScopeGroupId());

        SessionMessages.add(actionRequest, REQUEST_PROCESSED,
                PortletUtil.translate("successfully-deleted-x-task-records", taskRecords.size()));

        String mvcPath = ParamUtil.getString(actionRequest, "mvcPath");
        String tabs1 = ParamUtil.getString(actionRequest, "tabs1");
        String tabs2 = ParamUtil.getString(actionRequest, "tabs2");

        actionResponse.setRenderParameter("mvcPath", mvcPath);
        actionResponse.setRenderParameter("tabs1", tabs1);
        actionResponse.setRenderParameter("tabs2", tabs2);

    }

    /**
     *
     * @param actionRequest
     * @param actionResponse
     * @throws Exception
     */
    protected void deleteTaskRecords(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long taskRecordId = ParamUtil.getLong(actionRequest, "taskRecordId");

        long[] taskRecordIds = ParamUtil.getLongValues(actionRequest, "deleteTaskRecordIds");

        if (taskRecordId > 0) {
            taskRecordIds = new long[] { taskRecordId };
        }

        for (long id : taskRecordIds) {
            _taskRecordService.deleteTaskRecord(id);
        }

    }

    protected void deleteTempFileEntry(ActionRequest actionRequest, ActionResponse actionResponse, String folderName)
            throws Exception {

        _log.info("deleteTempFileEntry");

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

        try {
            String fileName = ParamUtil.getString(actionRequest, "fileName");

            _taskRecordService.deleteTempFileEntry(themeDisplay.getScopeGroupId(), folderName, fileName);

            jsonObject.put("deleted", Boolean.TRUE);
        } catch (Exception e) {
            String errorMessage = themeDisplay.translate("an-unexpected-error-occurred-while-deleting-the-file");

            jsonObject.put("deleted", Boolean.FALSE);
            jsonObject.put("errorMessage", errorMessage);
        }

        JSONPortletResponseUtil.writeJSON(actionRequest, actionResponse, jsonObject);

    }

    protected void deleteTempFileEntry(long groupId, String folderName) throws PortalException {

        _log.info("deleteTempFileEntry");

        String[] tempFileNames = _taskRecordService.getTempFileNames(groupId, folderName);

        for (String tempFileEntryName : tempFileNames) {
            _taskRecordService.deleteTempFileEntry(groupId, folderName, tempFileEntryName);
        }
    }

    @Override
    protected void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        if (SessionErrors.contains(renderRequest, PrincipalException.getNestedClasses())
                || SessionErrors.contains(renderRequest, NoSuchTaskRecordException.class)) {
            include("/error.jsp", renderRequest, renderResponse);
        } else {
            super.doDispatch(renderRequest, renderResponse);
        }
    }

    protected void download(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws Exception {

        HttpServletRequest request = PortalUtil.getHttpServletRequest(resourceRequest);

        PortletPreferences portletPreferences = resourceRequest.getPreferences();

        List<TaskRecord> taskRecords = getTaskRecords(request);

        Map<String, Object> contextObjects = new HashMap<>();

        contextObjects.put("taskRecords", taskRecords);

        String exportFileName = portletPreferences.getValue("export-file-name",
                _timetrackerConfiguration.exportFileName());
        String exportName = portletPreferences.getValue("export-name", _timetrackerConfiguration.exportName());
        String exportScript = portletPreferences.getValue("export-script", _timetrackerConfiguration.exportScript());

        String exportStr = null;

        try {
            exportStr = TemplateUtil.transform(contextObjects, exportScript, exportName, "ftl");
        } catch (Exception e) {
            exportStr = e.getCause().getMessage();
        }

        PortletResponseUtil.sendFile(resourceRequest, resourceResponse, exportFileName, exportStr.getBytes());

    }

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

    protected String getEditTaskRecordURL(ActionRequest actionRequest, ActionResponse actionResponse,
            TaskRecord taskRecord) throws Exception {

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        String editTaskRecordURL = getRedirect(actionRequest, actionResponse);

        if (Validator.isNull(editTaskRecordURL)) {
            editTaskRecordURL = PortalUtil.getLayoutFullURL(themeDisplay);
        }

        String namespace = actionResponse.getNamespace();
        String windowState = actionResponse.getWindowState().toString();

        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, "p_p_id", PortletKeys.TIMETRACKER);
        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, "p_p_state", windowState);
        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "mvcPath",
                templatePath + "edit_task_record.jsp");
        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "redirect",
                getRedirect(actionRequest, actionResponse));
        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "backURL",
                ParamUtil.getString(actionRequest, "backURL"));
        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "taskRecordId",
                taskRecord.getTaskRecordId());

        return editTaskRecordURL;
    }

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
     * @param resourceRequest
     * @param resourceResponse
     * @throws IOException
     * @throws SearchException
     * @since 1.1.5
     */
    protected void getSum(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws Exception {

        HttpServletRequest request = PortalUtil.getHttpServletRequest(resourceRequest);

        List<TaskRecord> taskRecords = getTaskRecords(request);

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

    /**
     * from com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand
     *
     * @param resourceRequest
     * @param path
     * @return
     */
    protected PortletRequestDispatcher getPortletRequestDispatcher(ResourceRequest resourceRequest, String path) {

        PortletConfig portletConfig = getPortletConfig(resourceRequest);

        PortletContext portletContext = portletConfig.getPortletContext();

        return portletContext.getRequestDispatcher(path);
    }

    protected void getTaskRecord(PortletRequest portletRequest) throws Exception {

        long taskRecordId = ParamUtil.getLong(portletRequest, "taskRecordId");

        if (taskRecordId <= 0) {
            return;
        }

        TaskRecord taskRecord = _taskRecordService.getTaskRecord(taskRecordId);

        portletRequest.setAttribute(TimetrackerWebKeys.TASK_RECORD, taskRecord);
    }

    /**
     * @param resourceRequest
     * @param resourceResponse
     * @return
     * @since 1.1.6
     * @throws SearchException
     */
    protected List<TaskRecord> getTaskRecords(HttpServletRequest request) throws Exception {

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        String description = ParamUtil.getString(request, "description");
        boolean advancedSearch = ParamUtil.getBoolean(request, "advancedSearch", false);
        boolean andOperator = ParamUtil.getBoolean(request, "andOperator", true);
        int end = ParamUtil.getInteger(request, "end");

        int fromDateDay = ParamUtil.getInteger(request, "fromDateDay");
        int fromDateMonth = ParamUtil.getInteger(request, "fromDateMonth");
        int fromDateYear = ParamUtil.getInteger(request, "fromDateYear");
        Date fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay, fromDateYear);

        String keywords = ParamUtil.getString(request, "keywords");
        String orderByCol = ParamUtil.getString(request, "orderByCol", "modifiedDate");
        String orderByType = ParamUtil.getString(request, "orderByType", "desc");
        long ownerUserId = ParamUtil.getLong(request, "ownerUserId");
        int start = ParamUtil.getInteger(request, "start");
        int status = ParamUtil.getInteger(request, Field.STATUS);

        int untilDateDay = ParamUtil.getInteger(request, "untilDateDay");
        int untilDateMonth = ParamUtil.getInteger(request, "untilDateMonth");
        int untilDateYear = ParamUtil.getInteger(request, "untilDateYear");
        Date untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay, untilDateYear);

        String workPackage = ParamUtil.getString(request, "workPackage");

        boolean reverse = "desc".equals(orderByType);

        Sort sort = new Sort(orderByCol, reverse);

        Hits hits = null;

        if (advancedSearch) {
            hits = TaskRecordServiceUtil.search(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), ownerUserId,
                    workPackage, description, status, fromDate, untilDate, null, andOperator, start, end, sort);
        } else {
            hits = TaskRecordServiceUtil.search(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), ownerUserId,
                    keywords, start, end, sort);
        }

        List<TaskRecord> taskRecords = TaskRecordUtil.getTaskRecords(hits);

        return taskRecords;

    }

    protected void handleUploadException(ActionRequest actionRequest, ActionResponse actionResponse, String folderName,
            Exception e) throws Exception {

        HttpServletResponse response = PortalUtil.getHttpServletResponse(actionResponse);

        response.setContentType(ContentTypes.TEXT_HTML);
        response.setStatus(HttpServletResponse.SC_OK);

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        deleteTempFileEntry(themeDisplay.getScopeGroupId(), folderName);

        JSONObject jsonObject = StagingUtil.getExceptionMessagesJSONObject(themeDisplay.getLocale(), e,
                (ExportImportConfiguration) null);

        JSONPortletResponseUtil.writeJSON(actionRequest, actionResponse, jsonObject);

        ServletResponseUtil.write(response, String.valueOf(jsonObject.getInt("status")));
    }

    protected void importTaskRecords(ActionRequest actionRequest, String folderName) throws Exception {

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        long groupId = ParamUtil.getLong(actionRequest, "groupId");

        FileEntry fileEntry = ExportImportHelperUtil.getTempFileEntry(groupId, themeDisplay.getUserId(), folderName);

        InputStream inputStream = null;

        try {
            inputStream = _dlFileEntryLocalService.getFileAsStream(fileEntry.getFileEntryId(), fileEntry.getVersion(),
                    false);

            importTaskRecords(actionRequest, fileEntry.getTitle(), inputStream);

            deleteTempFileEntry(groupId, folderName);

        } finally {
            StreamUtil.cleanUp(inputStream);
        }
    }

    protected void importTaskRecords(ActionRequest actionRequest, String fileName, InputStream inputStream)
            throws Exception {

        long groupId = ParamUtil.getLong(actionRequest, "groupId");

        ExportImportConfiguration exportImportConfiguration = getExportImportConfiguration(actionRequest);

        exportImportConfiguration.setName("TaskRecords");
        exportImportConfiguration.setGroupId(groupId);

        _taskRecordService.importTaskRecordsInBackground(exportImportConfiguration, inputStream);

    }

    protected void importTaskRecords(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
            throws Exception {

        String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);

        PortletRequestDispatcher portletRequestDispatcher = null;

        if (cmd.equals(Constants.IMPORT)) {

            portletRequestDispatcher = getPortletRequestDispatcher(resourceRequest, "/import/processes_list/view.jsp");

        } else {

            portletRequestDispatcher = getPortletRequestDispatcher(resourceRequest,
                    "/import/new_import/import_task_records_resources.jsp");
        }

        portletRequestDispatcher.include(resourceRequest, resourceResponse);
    }

    @Reference
    protected void setDLFileEntryLocalService(DLFileEntryLocalService dlFileEntryLocalService) {
        this._dlFileEntryLocalService = dlFileEntryLocalService;
    }

    @Reference(unbind = "-")
    protected void setExportImportConfigurationLocalService(
            ExportImportConfigurationLocalService exportImportConfigurationLocalService) {
        _exportImportConfigurationLocalService = exportImportConfigurationLocalService;
    }

    @Reference(unbind = "-")
    protected void setExportImportService(ExportImportService exportImportService) {
        _exportImportService = exportImportService;
    }

    @Reference(unbind = "-")
    protected void setLayoutService(LayoutService layoutService) {
        _layoutService = layoutService;
    }

    @Reference(unbind = "-")
    protected void setTaskRecordService(TaskRecordService taskRecordService) {
        this._taskRecordService = taskRecordService;
    }


    /**
     *
     * @param actionRequest
     * @param actionResponse
     * @throws Exception
     */
    protected void updateTaskRecord(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long taskRecordId = ParamUtil.getLong(actionRequest, "taskRecordId");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(TaskRecord.class.getName(), actionRequest);

        String workPackage = ParamUtil.getString(actionRequest, "workPackage");
        String description = ParamUtil.getString(actionRequest, "description");
        String ticketURL = ParamUtil.getString(actionRequest, "ticketURL");
        int durationInMinutes = ParamUtil.getInteger(actionRequest, "duration");
        long duration = durationInMinutes * 60 * 1000;
        int status = ParamUtil.getInteger(actionRequest, "status");

        int fromDateDay = ParamUtil.getInteger(actionRequest, "fromDateDay");
        int fromDateMonth = ParamUtil.getInteger(actionRequest, "fromDateMonth");
        int fromDateYear = ParamUtil.getInteger(actionRequest, "fromDateYear");
        int fromDateHour = ParamUtil.getInteger(actionRequest, "fromDateHour");
        int fromDateMinute = ParamUtil.getInteger(actionRequest, "fromDateMinute");

        // TODO: clean this up!
        // Create the untilDate with the date values of
        // the fromDate, because we want the user to
        // have to select only one date.
        int untilDateDay = ParamUtil.getInteger(actionRequest, "fromDateDay");
        int untilDateMonth = ParamUtil.getInteger(actionRequest, "fromDateMonth");
        int untilDateYear = ParamUtil.getInteger(actionRequest, "fromDateYear");
        int untilDateHour = ParamUtil.getInteger(actionRequest, "untilDateHour");
        int untilDateMinute = ParamUtil.getInteger(actionRequest, "untilDateMinute");

        Date fromDate = null;

        try {
            fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay, fromDateYear, fromDateHour, fromDateMinute,
                    TaskRecordUntilDateException.class);
        } catch (Exception e) {
            _log.error(e);
        }

        Date untilDate = null;

        try {
            untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay, untilDateYear, untilDateHour, untilDateMinute,
                    TaskRecordFromDateException.class);
        } catch (Exception e) {
            _log.error(e);
        }

        long fromTime = fromDate.getTime();
        long untilTime = untilDate.getTime();

        if (fromTime > untilTime) {
            untilDate = new Date(untilTime + 1000 * 60 * 60 * 24);
            untilTime = untilDate.getTime();
        }

        if (duration == 0) {
            duration = untilTime - fromTime;
        }

        TaskRecord taskRecord = null;

        if (taskRecordId <= 0) {

            // Add taskRecord

            taskRecord = _taskRecordService.addTaskRecord(workPackage, description, ticketURL, untilDate, fromDate,
                    status, duration, serviceContext);

        } else {

            // Update taskRecord

            taskRecord = _taskRecordService.updateTaskRecord(taskRecordId, workPackage, description, ticketURL,
                    untilDate, fromDate, status, duration, serviceContext);
        }

        String redirect = getEditTaskRecordURL(actionRequest, actionResponse, taskRecord);

        actionRequest.setAttribute(WebKeys.REDIRECT, redirect);

        actionRequest.setAttribute(TimetrackerWebKeys.TASK_RECORD, taskRecord);
    }

    protected void validateFile(ActionRequest actionRequest, ActionResponse actionResponse, String folderName)
            throws Exception {

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        long groupId = ParamUtil.getLong(actionRequest, "groupId");

        FileEntry fileEntry = ExportImportHelperUtil.getTempFileEntry(groupId, themeDisplay.getUserId(), folderName);

        InputStream inputStream = null;

        try {
            inputStream = _dlFileEntryLocalService.getFileAsStream(fileEntry.getFileEntryId(), fileEntry.getVersion(),
                    false);

            MissingReferences missingReferences = validateFile(actionRequest, inputStream);

            Map<String, MissingReference> weakMissingReferences = missingReferences.getWeakMissingReferences();

            if (weakMissingReferences.isEmpty()) {
                return;
            }

            JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

            if ((weakMissingReferences != null) && !weakMissingReferences.isEmpty()) {

                jsonObject.put("warningMessages",
                        StagingUtil.getWarningMessagesJSONArray(themeDisplay.getLocale(), weakMissingReferences));
            }

            JSONPortletResponseUtil.writeJSON(actionRequest, actionResponse, jsonObject);
        } finally {
            StreamUtil.cleanUp(inputStream);
        }
    }

    protected MissingReferences validateFile(ActionRequest actionRequest, InputStream inputStream) throws Exception {

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        long groupId = ParamUtil.getLong(actionRequest, "groupId");
        boolean privateLayout = ParamUtil.getBoolean(actionRequest, "privateLayout");

        Map<String, Serializable> importLayoutSettingsMap = ExportImportConfigurationSettingsMapFactory
                .buildImportLayoutSettingsMap(themeDisplay.getUserId(), groupId, privateLayout, null,
                        actionRequest.getParameterMap(), themeDisplay.getLocale(), themeDisplay.getTimeZone());

        ExportImportConfiguration exportImportConfiguration = _exportImportConfigurationLocalService
                .addDraftExportImportConfiguration(themeDisplay.getUserId(),
                        ExportImportConfigurationConstants.TYPE_IMPORT_TASK_RECORDS, importLayoutSettingsMap);

        return _exportImportService.validateImportLayoutsFile(exportImportConfiguration, inputStream);
    }

    private DLFileEntryLocalService _dlFileEntryLocalService;
    private ExportImportConfigurationLocalService _exportImportConfigurationLocalService;
    private ExportImportService _exportImportService;
    private LayoutService _layoutService;
    private TaskRecordService _taskRecordService;

    private volatile TimetrackerConfiguration _timetrackerConfiguration;

    private static final String REQUEST_PROCESSED = "request_processed";

    private static final Log _log = LogFactoryUtil.getLog(TimetrackerPortlet.class.getName());

}
