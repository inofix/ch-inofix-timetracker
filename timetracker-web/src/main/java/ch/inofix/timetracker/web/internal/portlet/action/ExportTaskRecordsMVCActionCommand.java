package ch.inofix.timetracker.web.internal.portlet.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.exception.LARFileNameException;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.portal.kernel.exception.NoSuchBackgroundTaskException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
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
import ch.inofix.timetracker.service.TaskRecordService;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-14 18:10
 * @modified 2017-11-14 18:10
 * @version 1.0.0
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

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

        _log.info("doProcessAction");
        _log.info("cmd = " + cmd);

        // try {

        if (cmd.equals(Constants.DELETE)) {
            deleteBackgroundTasks(actionRequest, actionResponse);
        } else if (cmd.equals(Constants.EXPORT)) {
            exportTaskRecords(actionRequest, actionResponse);
        }

        // if (Validator.isNotNull(cmd)) {
        // String redirect = ParamUtil.getString(actionRequest, "redirect");
        // if (taskRecord != null) {
        //
        // redirect = getSaveAndContinueRedirect(actionRequest, taskRecord,
        // themeDisplay.getLayout(),
        // redirect);
        //
        // sendRedirect(actionRequest, actionResponse, redirect);
        // }
        // }

        // } catch (NoSuchTaskRecordException | PrincipalException e) {
        //
        // SessionErrors.add(actionRequest, e.getClass());
        //
        // actionResponse.setRenderParameter("mvcPath", "/error.jsp");
        //
        // // TODO: Define set of exceptions reported back to user. For an
        // // example, see EditCategoryMVCActionCommand.java.
        //
        // } catch (Exception e) {
        //
        // SessionErrors.add(actionRequest, e.getClass());
        // }

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

        String tabs1 = ParamUtil.getString(actionRequest, "tabs1");
        String tabs2 = ParamUtil.getString(actionRequest, "tabs2");

        actionResponse.setRenderParameter("tabs1", tabs1);
        actionResponse.setRenderParameter("tabs2", tabs2);

        addSuccessMessage(actionRequest, actionResponse);

    }

    protected void exportTaskRecords(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        _log.info("exportTaskRecords");

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

            _log.info("exportImportConfiguration = " + exportImportConfiguration);

            _taskRecordService.exportTaskRecordsAsFileInBackground(themeDisplay.getUserId(), exportImportConfiguration);

            sendRedirect(actionRequest, actionResponse);

        } catch (Exception e) {
            SessionErrors.add(actionRequest, e.getClass());

            if (!(e instanceof LARFileNameException)) {
                _log.error(e, e);
            }
        }

    }

    protected ExportImportConfiguration getExportImportConfiguration(ActionRequest actionRequest) throws Exception {

        Map<String, Serializable> exportLayoutSettingsMap = null;

        long exportImportConfigurationId = ParamUtil.getLong(actionRequest, "exportImportConfigurationId");

        if (exportImportConfigurationId > 0) {
            ExportImportConfiguration exportImportConfiguration = _exportImportConfigurationLocalService
                    .fetchExportImportConfiguration(exportImportConfigurationId);

            if (exportImportConfiguration != null) {
                exportLayoutSettingsMap = exportImportConfiguration.getSettingsMap();
            }
        }

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        boolean privateLayout = ParamUtil.getBoolean(actionRequest, "privateLayout");

        if (exportLayoutSettingsMap == null) {
            long groupId = ParamUtil.getLong(actionRequest, "groupId");
            long[] layoutIds = getLayoutIds(actionRequest);

            // TODO: build settingsMap without reference to layouts
            exportLayoutSettingsMap = ExportImportConfigurationSettingsMapFactory.buildExportLayoutSettingsMap(
                    themeDisplay.getUserId(), groupId, privateLayout, layoutIds, actionRequest.getParameterMap(),
                    themeDisplay.getLocale(), themeDisplay.getTimeZone());
        }

        String taskName = ParamUtil.getString(actionRequest, "name");

        if (Validator.isNull(taskName)) {
            if (privateLayout) {
                taskName = LanguageUtil.get(actionRequest.getLocale(), "private-pages");
            } else {
                taskName = LanguageUtil.get(actionRequest.getLocale(), "public-pages");
            }
        }

        return _exportImportConfigurationLocalService.addDraftExportImportConfiguration(themeDisplay.getUserId(),
                taskName, ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT, exportLayoutSettingsMap);
    }

    protected long[] getLayoutIds(PortletRequest portletRequest) throws Exception {

        Set<Layout> layouts = new LinkedHashSet<>();

        Map<Long, Boolean> layoutIdMap = ExportImportHelperUtil.getLayoutIdMap(portletRequest);

        // for (Map.Entry<Long, Boolean> entry : layoutIdMap.entrySet()) {
        // long plid = GetterUtil.getLong(String.valueOf(entry.getKey()));
        // boolean includeChildren = entry.getValue();
        //
        // Layout layout = _layoutLocalService.getLayout(plid);
        //
        // if (!layouts.contains(layout)) {
        // layouts.add(layout);
        // }
        //
        // if (includeChildren) {
        // layouts.addAll(layout.getAllChildren());
        // }
        // }

        return ExportImportHelperUtil.getLayoutIds(new ArrayList<Layout>(layouts));
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
