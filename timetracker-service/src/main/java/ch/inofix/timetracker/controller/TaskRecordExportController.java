package ch.inofix.timetracker.controller;

import static com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleConstants.EVENT_LAYOUT_EXPORT_FAILED;
import static com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleConstants.EVENT_LAYOUT_EXPORT_STARTED;
import static com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleConstants.EVENT_LAYOUT_EXPORT_SUCCEEDED;
import static com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleConstants.PROCESS_FLAG_LAYOUT_EXPORT_IN_PROCESS;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;

// TODO
//import org.apache.commons.lang.time.StopWatch;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.controller.PortletExportController;
import com.liferay.exportimport.kernel.controller.ExportController;
import com.liferay.exportimport.kernel.controller.ExportImportController;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerStatusMessageSenderUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManager;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactoryUtil;

import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalService;

/**
 * @author Christian Berndt
 * @created 2017-04-21 19:23
 * @modified 2017-04-21 19:23
 * @version 1.0.0
 */
@Component(immediate = true, property = { "model.class.name=ch.inofix.timetracker.model.TaskRecord" }, service = {
        ExportImportController.class, TaskRecordExportController.class })
public class TaskRecordExportController implements ExportController {

    @Override
    public File export(ExportImportConfiguration exportImportConfiguration) throws Exception {

        _log.info("export");

        PortletDataContext portletDataContext = null;

        try {
            // TODO
            // ExportImportThreadLocal.setTaskRecordExportInProcess(true);

            exportImportConfiguration.getSettingsMap();

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_LAYOUT_EXPORT_STARTED, getProcessFlag(),
                    PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext));

            File file = doExport(exportImportConfiguration);

            // TODO
            // ExportImportThreadLocal.setTaskRecordExportInProcess(false);

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_LAYOUT_EXPORT_SUCCEEDED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext));

            return file;

        } catch (Throwable t) {

            _log.error(t);

            // TODO
            // ExportImportThreadLocal.setTaskRecordExportInProcess(false);

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_LAYOUT_EXPORT_FAILED, getProcessFlag(),
                    PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext), t);

            throw t;
        }
    }

    protected File doExport(ExportImportConfiguration exportImportConfiguration) throws Exception {

        _log.info("doExport");

        // Map<String, String[]> parameterMap =
        // portletDataContext.getParameterMap();

        // boolean ignoreLastPublishDate = MapUtil.getBoolean(parameterMap,
        // PortletDataHandlerKeys.IGNORE_LAST_PUBLISH_DATE);
        // MapUtil.getBoolean(parameterMap,
        // PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS);
        // MapUtil.getBoolean(parameterMap,
        // PortletDataHandlerKeys.LAYOUT_SET_SETTINGS);
        // MapUtil.getBoolean(parameterMap, PortletDataHandlerKeys.LOGO);
        // boolean permissions = MapUtil.getBoolean(parameterMap,
        // PortletDataHandlerKeys.PERMISSIONS);
        //
        // if (_log.isDebugEnabled()) {
        // _log.debug("Export permissions " + permissions);
        // }

        // TODO
        // TaskRecord taskRecord =
        // _taskRecordLocalService.getTaskRecord(taskRecordIds[0]);

        // TODO
        long companyId = PortalUtil.getDefaultCompanyId();
        long defaultUserId = _userLocalService.getDefaultUserId(companyId);

        ServiceContext serviceContext = ServiceContextThreadLocal.popServiceContext();

        if (serviceContext == null) {
            serviceContext = new ServiceContext();
        }

        serviceContext.setCompanyId(companyId);
        serviceContext.setSignedIn(false);
        serviceContext.setUserId(defaultUserId);

        serviceContext.setAttribute("exporting", Boolean.TRUE);

        ServiceContextThreadLocal.pushServiceContext(serviceContext);

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        Document document = SAXReaderUtil.createDocument();

        Element rootElement = document.addElement("root");

        Element headerElement = rootElement.addElement("header");

        headerElement.addAttribute("available-locales",
                StringUtil.merge(LanguageUtil.getAvailableLocales(exportImportConfiguration.getGroupId())));

        headerElement.addAttribute("build-number", String.valueOf(ReleaseInfo.getBuildNumber()));

        Bundle bundle = FrameworkUtil.getBundle(TaskRecordExportController.class);

        Version version = bundle.getVersion();

        headerElement.addAttribute("bundle-version", String.valueOf(version));

        headerElement.addAttribute("export-date", Time.getRFC822());

        // TODO
        // if (portletDataContext.hasDateRange()) {
        // headerElement.addAttribute("start-date",
        // String.valueOf(portletDataContext.getStartDate()));
        // headerElement.addAttribute("end-date",
        // String.valueOf(portletDataContext.getEndDate()));
        // }

        // TODO
        // Group group = taskRecordSet.getGroup();

        String type = "task-record";

        headerElement.addAttribute("type", type);

        // Element missingReferencesElement =
        // rootElement.addElement("missing-references");

        Map<String, Object[]> portletIds = new LinkedHashMap<>();

        long groupId = exportImportConfiguration.getGroupId();

        _log.info("groupId = " + groupId);

        // Group group = GroupLocalServiceUtil.getGroup(groupId);

        _taskRecordLocalService.getGroupTaskRecords(groupId);

        //
        // // Calculate the amount of exported data
        //
        // if (BackgroundTaskThreadLocal.hasBackgroundTask()) {
        // PortletDataHandler portletDataHandler =
        // portlet.getPortletDataHandlerInstance();
        //
        // portletDataHandler.prepareManifestSummary(portletDataContext);
        // }

        // Scoped data

        if (BackgroundTaskThreadLocal.hasBackgroundTask()) {

            ManifestSummary manifestSummary = new ManifestSummary();
            // ManifestSummary manifestSummary =
            // portletDataContext.getManifestSummary();

            PortletDataHandlerStatusMessageSenderUtil.sendStatusMessage("taskRecord",
                    ArrayUtil.toStringArray(portletIds.keySet()), manifestSummary);

            manifestSummary.resetCounters();
        }

        _log.info("Exporting task-records takes " + stopWatch.getTime() + " ms");

        // Export actual data

        ZipWriter zipWriter = ZipWriterFactoryUtil.getZipWriter();

        zipWriter.addEntry("foo", "bar");

        File file = zipWriter.getFile();

        _log.info(file.getName());
        _log.info(file.getAbsolutePath());

        return file;

    }

    protected int getProcessFlag() {
        // TODO
        // if (ExportImportThreadLocal.isTaskRecordStagingInProcess()) {
        // return PROCESS_FLAG_LAYOUT_STAGING_IN_PROCESS;
        // }

        return PROCESS_FLAG_LAYOUT_EXPORT_IN_PROCESS;
    }

    protected boolean prepareTaskRecordStagingHandler(PortletDataContext portletDataContext, TaskRecord taskRecord) {

        MapUtil.getBoolean(portletDataContext.getParameterMap(), "exportLAR");

        return true;
    }

    @Reference(unbind = "-")
    protected void setExportImportLifecycleManager(ExportImportLifecycleManager exportImportLifecycleManager) {

        _exportImportLifecycleManager = exportImportLifecycleManager;
    }

    @Reference(unbind = "-")
    protected void setGroupLocalService(GroupLocalService groupLocalService) {
    }

    @Reference(unbind = "-")
    protected void setImageLocalService(ImageLocalService imageLocalService) {
    }

    @Reference(unbind = "-")
    protected void setTaskRecordLocalService(TaskRecordLocalService taskRecordLocalService) {

        _taskRecordLocalService = taskRecordLocalService;
    }

    @Reference(unbind = "-")
    protected void setPortletExportController(PortletExportController portletExportController) {
    }

    @Reference(unbind = "-")
    protected void setUserLocalService(UserLocalService userLocalService) {
        _userLocalService = userLocalService;
    }

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordExportController.class);

    private ExportImportLifecycleManager _exportImportLifecycleManager;
    private TaskRecordLocalService _taskRecordLocalService;
    private UserLocalService _userLocalService;

}