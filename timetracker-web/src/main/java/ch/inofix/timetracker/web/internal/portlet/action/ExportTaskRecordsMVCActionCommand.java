package ch.inofix.timetracker.web.internal.portlet.action;

import java.io.Serializable;
import java.util.Map;
import java.util.TimeZone;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.LARFileNameException;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.portal.kernel.exception.NoSuchBackgroundTaskException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.internal.exportimport.configuration.ExportImportTaskRecordsConfigurationSettingsMapFactory;
import ch.inofix.timetracker.service.TaskRecordService;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-14 18:10
 * @modified 2017-11-17 22:30
 * @version 1.0.1
 *
 */
@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + PortletKeys.TIMETRACKER,
        "mvc.command.name=exportTaskRecords"
    },
    service = MVCActionCommand.class
)
public class ExportTaskRecordsMVCActionCommand extends BaseMVCActionCommand {

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        _log.info("doProcessAction()");
        
        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

        _log.info("cmd = " + cmd);

        if (cmd.equals(Constants.DELETE)) {
            deleteBackgroundTasks(actionRequest, actionResponse);
        } else if (cmd.equals(Constants.EXPORT)) {
            exportTaskRecords(actionRequest, actionResponse);
        }

        String redirect = ParamUtil.getString(actionRequest, "redirect");

        if (Validator.isNotNull(redirect)) {
            sendRedirect(actionRequest, actionResponse, redirect);
        }
    }

    protected void deleteBackgroundTasks(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        _log.info("deleteBackgroundTasks()");

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
        long groupId = themeDisplay.getScopeGroupId();

        try {
            long[] backgroundTaskIds = ParamUtil.getLongValues(actionRequest, "deleteBackgroundTaskIds");

            for (long backgroundTaskId : backgroundTaskIds) {
                _taskRecordService.deleteBackgroundTask(groupId, backgroundTaskId);
            }
        } catch (Exception e) {
            if (e instanceof NoSuchBackgroundTaskException || e instanceof PrincipalException) {

                SessionErrors.add(actionRequest, e.getClass());

                actionResponse.setRenderParameter("mvcPath", "/error.jsp");

                hideDefaultSuccessMessage(actionRequest);

            } else {
                throw e;
            }
        }

        addSuccessMessage(actionRequest, actionResponse);

    }

    protected void exportTaskRecords(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        
        _log.info("exportTaskRecords()");

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        hideDefaultSuccessMessage(actionRequest);

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

        if (Validator.isNull(cmd)) {
            SessionMessages.add(actionRequest,
                    _portal.getPortletId(actionRequest) + SessionMessages.KEY_SUFFIX_FORCE_SEND_REDIRECT);

            hideDefaultSuccessMessage(actionRequest);

            return;
        }

        try {

            ExportImportConfiguration exportImportConfiguration = getExportImportConfiguration(actionRequest);

            _taskRecordService.exportTaskRecordsAsFileInBackground(themeDisplay.getUserId(), exportImportConfiguration);

            sendRedirect(actionRequest, actionResponse);

        } catch (Exception e) {
            SessionErrors.add(actionRequest, e.getClass());
            
            // TODO: remove LARFileNameException dependency
            if (!(e instanceof LARFileNameException)) {
                _log.error(e, e);
            }
        }
    }

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

            exportTaskRecordsSettingsMap = ExportImportTaskRecordsConfigurationSettingsMapFactory
                    .buildExportTaskRecordsSettingsMap(themeDisplay.getCompanyId(), themeDisplay.getUserId(),
                            themeDisplay.getPlid(), themeDisplay.getScopeGroupId(), PortletKeys.TIMETRACKER,
                            actionRequest.getParameterMap(), themeDisplay.getLocale(), TimeZone.getDefault(), null);
        }

        String taskName = ParamUtil.getString(actionRequest, "name");

        if (Validator.isNull(taskName)) {
            taskName = "task-records";
        }

        // TODO: remove dependendency from ExportImportConfigurationConstants
        return _exportImportConfigurationLocalService.addDraftExportImportConfiguration(themeDisplay.getUserId(),
                taskName, ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT, exportTaskRecordsSettingsMap);
    }

    @Reference(unbind = "-")
    protected void setExportImportConfigurationLocalService(
            ExportImportConfigurationLocalService exportImportConfigurationLocalService) {

        _exportImportConfigurationLocalService = exportImportConfigurationLocalService;
    }
    
    @Reference(unbind = "-")
    protected void setTaskRecordService(TaskRecordService taskRecordService) {
        this._taskRecordService = taskRecordService;
    }

    private static final Log _log = LogFactoryUtil.getLog(ExportTaskRecordsMVCActionCommand.class);

    private ExportImportConfigurationLocalService _exportImportConfigurationLocalService;
    private TaskRecordService _taskRecordService;

    @Reference
    private Portal _portal;
    
}
