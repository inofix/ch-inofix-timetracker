package ch.inofix.timetracker.controller;

import static ch.inofix.timetracker.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_TASK_RECORDS_IMPORT_FAILED;
import static ch.inofix.timetracker.internal.exportimport.util.ExportImportLifecycleConstants.PROCESS_FLAG_TASK_RECORDS_IMPORT_IN_PROCESS;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.controller.ExportImportController;
import com.liferay.exportimport.kernel.controller.ImportController;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManager;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import ch.inofix.timetracker.internal.exportimport.util.ExportImportThreadLocal;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalService;

/**
 *
 * @author Christian Berndt
 * @created 2017-06-04 18:07
 * @modified 2017-06-09 18:14
 * @version 1.0.1
 *
 */
@Component(immediate = true, property = { "model.class.name=ch.inofix.timetracker.model.TaskRecord" }, service = {
        ExportImportController.class, TaskRecordImportController.class })
public class TaskRecordImportController extends BaseExportImportController implements ImportController {

    public TaskRecordImportController() {
        initXStream();
    }

    @Override
    public void importDataDeletions(ExportImportConfiguration exportImportConfiguration, File file) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void importFile(ExportImportConfiguration exportImportConfiguration, File file) throws Exception {

        _log.info("importFile");

        PortletDataContext portletDataContext = null;

        try {
            ExportImportThreadLocal.setTaskRecordImportInProcess(true);

            // TODO: process import-settings
            // Map<String, Serializable> settingsMap =
            // exportImportConfiguration.getSettingsMap();

            doImportFile(file);
            ExportImportThreadLocal.setTaskRecordImportInProcess(false);

        } catch (Throwable t) {
            ExportImportThreadLocal.setTaskRecordImportInProcess(false);

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_TASK_RECORDS_IMPORT_FAILED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext), t);

            throw t;
        }
    }

    @Override
    public MissingReferences validateFile(ExportImportConfiguration exportImportConfiguration, File file)
            throws Exception {

        throw new UnsupportedOperationException();

    }

    protected void doImportFile(File file) throws Exception {

        _log.info("doImportFile");

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        Document document = SAXReaderUtil.read(file);

        // _log.info(document.asXML());

        List<Node> nodes = document.selectNodes("TaskRecords/ch.inofix.timetracker.model.impl.TaskRecordImpl");

        _log.info(nodes.size());

        for (Node node : nodes) {

            String xml = node.asXML();

            _log.info(xml);

            _xStream.setClassLoader(TaskRecordImportController.class.getClassLoader());

            TaskRecord importTaskRecord = (TaskRecord) _xStream.fromXML(xml);

            _log.info(importTaskRecord);

            // TODO: process import configuration - have a look at
            // PortletDataContextImpl

            TaskRecord taskRecord = null;

            // taskRecord =
            // _taskRecordLocalService.fetchTaskRecord(importTaskRecord.getTaskRecordId());

            long userId = 0;
            String workPackage = null;
            String description = null;
            String ticketURL = null;
            Date fromDate = null;
            Date untilDate = null;
            int status = -1;
            long duration = 0;

            ServiceContext serviceContext = new ServiceContext();
            serviceContext.setScopeGroupId(importTaskRecord.getGroupId());

            if (taskRecord == null) {

                // insert as new

                userId = importTaskRecord.getUserId();
                workPackage = importTaskRecord.getWorkPackage();
                description = importTaskRecord.getDescription();
                ticketURL = importTaskRecord.getTicketURL();
                fromDate = importTaskRecord.getFromDate();
                untilDate = importTaskRecord.getUntilDate();
                status = importTaskRecord.getStatus();
                duration = importTaskRecord.getDuration();

                taskRecord = _taskRecordLocalService.addTaskRecord(userId, workPackage, description, ticketURL,
                        untilDate, fromDate, status, duration, serviceContext);

            }

        }

        if (_log.isInfoEnabled()) {
            _log.info("Importing taskRecords takes " + stopWatch.getTime() + " ms");
        }

    }

    protected int getProcessFlag() {

        return PROCESS_FLAG_TASK_RECORDS_IMPORT_IN_PROCESS;
    }

    @Reference(unbind = "-")
    protected void setExportImportLifecycleManager(ExportImportLifecycleManager exportImportLifecycleManager) {

        _exportImportLifecycleManager = exportImportLifecycleManager;
    }

    @Reference(unbind = "-")
    protected void setTaskRecordLocalService(TaskRecordLocalService taskRecordLocalService) {
        this._taskRecordLocalService = taskRecordLocalService;
    }

    private ExportImportLifecycleManager _exportImportLifecycleManager;
    private TaskRecordLocalService _taskRecordLocalService;

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordImportController.class.getName());

}
