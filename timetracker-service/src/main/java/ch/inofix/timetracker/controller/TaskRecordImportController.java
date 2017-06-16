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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
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
 * @modified 2017-06-16 16:35
 * @version 1.0.3
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

        PortletDataContext portletDataContext = null;

        try {
            ExportImportThreadLocal.setTaskRecordImportInProcess(true);

            // TODO: process import-settings
            // Map<String, Serializable> settingsMap =
            // exportImportConfiguration.getSettingsMap();

            doImportFile(file, exportImportConfiguration.getUserId(), exportImportConfiguration.getGroupId());
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

    protected void doImportFile(File file, long userId, long groupId) throws Exception {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        int numAdded = 0;

        Document document = SAXReaderUtil.read(file);

        List<Node> nodes = document.selectNodes("TaskRecords/ch.inofix.timetracker.model.impl.TaskRecordImpl");

        for (Node node : nodes) {

            String xml = node.asXML();

            _xStream.setClassLoader(TaskRecordImportController.class.getClassLoader());

            TaskRecord importTaskRecord = (TaskRecord) _xStream.fromXML(xml);

            // TODO: process import configuration - have a look at
            // PortletDataContextImpl

            String description = null;
            long duration = 0;
            Date fromDate = null;
            int status = -1;
            long taskRecordId = importTaskRecord.getTaskRecordId();
            String ticketURL = null;
            Date untilDate = null;
            String workPackage = null;

            ServiceContext serviceContext = new ServiceContext();

            Group group = null;
            try {
                group = GroupLocalServiceUtil.getGroup(importTaskRecord.getGroupId());
            } catch (Exception e) {
                _log.warn(e.getMessage());
            }

            if (group != null) {
                groupId = importTaskRecord.getGroupId();
            }

            serviceContext.setScopeGroupId(groupId);

            if (taskRecordId == 0) {

                // no taskRecordId: insert as new of the importing user

                description = importTaskRecord.getDescription();
                duration = importTaskRecord.getDuration();
                fromDate = importTaskRecord.getFromDate();
                status = importTaskRecord.getStatus();
                ticketURL = importTaskRecord.getTicketURL();
                untilDate = importTaskRecord.getUntilDate();
                workPackage = importTaskRecord.getWorkPackage();

                _taskRecordLocalService.addTaskRecord(userId, workPackage, description, ticketURL, untilDate, fromDate,
                        status, duration, serviceContext);

                numAdded++;

            }
        }

        if (_log.isInfoEnabled()) {
            _log.info("Importing taskRecords takes " + stopWatch.getTime() + " ms.");
            _log.info("Added " + numAdded + " taskRecords as new, since they did not have a taskRecordId.");
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
