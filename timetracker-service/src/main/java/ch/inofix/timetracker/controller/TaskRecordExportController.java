package ch.inofix.timetracker.controller;

import static ch.inofix.timetracker.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_TASK_RECORDS_EXPORT_FAILED;
import static ch.inofix.timetracker.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_TASK_RECORDS_EXPORT_STARTED;
import static ch.inofix.timetracker.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_TASK_RECORDS_EXPORT_SUCCEEDED;
import static ch.inofix.timetracker.internal.exportimport.util.ExportImportLifecycleConstants.PROCESS_FLAG_TASK_RECORDS_EXPORT_IN_PROCESS;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.controller.ExportController;
import com.liferay.exportimport.kernel.controller.ExportImportController;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManager;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.zip.ZipWriter;

import ch.inofix.timetracker.internal.exportimport.util.ExportImportThreadLocal;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalService;

/**
 * @author Christian Berndt
 * @created 2017-04-21 19:23
 * @modified 2017-11-17 22:39
 * @version 1.0.5
 */
@Component(
    immediate = true, 
    property = { "model.class.name=ch.inofix.timetracker.model.TaskRecord" }, 
    service = {
        ExportImportController.class, 
        TaskRecordExportController.class 
    }
)
public class TaskRecordExportController extends BaseExportImportController implements ExportController {

    public TaskRecordExportController() {
        initXStream();
    }

    @Override
    public File export(ExportImportConfiguration exportImportConfiguration) throws Exception {

        PortletDataContext portletDataContext = null;

        try {

            ExportImportThreadLocal.setTaskRecordExportInProcess(true);

            portletDataContext = getPortletDataContext(exportImportConfiguration);

            exportImportConfiguration.getSettingsMap();

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_TASK_RECORDS_EXPORT_STARTED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext));

            File file = doExport(portletDataContext);

            ExportImportThreadLocal.setTaskRecordExportInProcess(false);

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_TASK_RECORDS_EXPORT_SUCCEEDED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext));

            return file;

        } catch (Throwable t) {

            _log.error(t);

            ExportImportThreadLocal.setTaskRecordExportInProcess(false);

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_TASK_RECORDS_EXPORT_FAILED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext), t);

            throw t;
        }
    }

    protected File doExport(PortletDataContext portletDataContext) throws Exception {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        final StringBuilder sb = new StringBuilder();

        sb.append("<TaskRecords>");
        sb.append(StringPool.NEW_LINE);

        ActionableDynamicQuery actionableDynamicQuery = _taskRecordLocalService.getActionableDynamicQuery();

        actionableDynamicQuery.setGroupId(portletDataContext.getGroupId());

        // TODO: process date-range of portletDataContext
        actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<TaskRecord>() {

            @Override
            public void performAction(TaskRecord taskRecord) {
                String xml = _xStream.toXML(taskRecord);
                sb.append(xml);
                sb.append(StringPool.NEW_LINE);
            }

        });

        actionableDynamicQuery.performActions();

        sb.append("</TaskRecords>");

        if (_log.isInfoEnabled()) {
            _log.info("Exporting taskRecords takes " + stopWatch.getTime() + " ms");
        }

        portletDataContext.addZipEntry("/TaskRecords.xml", sb.toString());

        ZipWriter zipWriter = portletDataContext.getZipWriter();

        return zipWriter.getFile();

    }

    protected PortletDataContext getPortletDataContext(ExportImportConfiguration exportImportConfiguration)
            throws PortalException {

        Map<String, Serializable> settingsMap = exportImportConfiguration.getSettingsMap();

        long companyId = MapUtil.getLong(settingsMap, "companyId");
        long sourceGroupId = MapUtil.getLong(settingsMap, "sourceGroupId");
        String portletId = MapUtil.getString(settingsMap, "portletId");
        Map<String, String[]> parameterMap = (Map<String, String[]>) settingsMap.get("parameterMap");
        DateRange dateRange = ExportImportDateUtil.getDateRange(exportImportConfiguration);

        ZipWriter zipWriter = ExportImportHelperUtil.getPortletZipWriter(portletId);

        PortletDataContext portletDataContext = PortletDataContextFactoryUtil.createExportPortletDataContext(companyId,
                sourceGroupId, parameterMap, dateRange.getStartDate(), dateRange.getEndDate(), zipWriter);

        portletDataContext.setPortletId(portletId);

        return portletDataContext;
    }

    protected int getProcessFlag() {
        return PROCESS_FLAG_TASK_RECORDS_EXPORT_IN_PROCESS;
    }

    @Reference(unbind = "-")
    protected void setExportImportLifecycleManager(ExportImportLifecycleManager exportImportLifecycleManager) {
        _exportImportLifecycleManager = exportImportLifecycleManager;
    }

    @Reference(unbind = "-")
    protected void setTaskRecordLocalService(TaskRecordLocalService taskRecordLocalService) {

        _taskRecordLocalService = taskRecordLocalService;
    }

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordExportController.class);

    private ExportImportLifecycleManager _exportImportLifecycleManager;
    private TaskRecordLocalService _taskRecordLocalService;

}
