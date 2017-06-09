package ch.inofix.timetracker.internal.exportimport.data.handler;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.exportimport.lar.BaseStagedModelDataHandler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.xml.Element;

import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalService;

/**
 *
 * @author Christian Berndt
 * @created 2016-04-17 23:35
 * @modified 2016-06-09 18:17
 * @version 1.0.1
 *
 */
@Component(immediate = true, service = StagedModelDataHandler.class)
public class TaskRecordStagedModelDataHandler extends BaseStagedModelDataHandler<TaskRecord> {

    public static final String[] CLASS_NAMES = { TaskRecord.class.getName() };

    @Override
    public void deleteStagedModel(TaskRecord taskRecord) throws PortalException {
        _taskRecordLocalService.deleteTaskRecord(taskRecord);
    }

    @Override
    public void deleteStagedModel(String uuid, long groupId, String className, String extraData)
            throws PortalException {

        TaskRecord taskRecord = fetchStagedModelByUuidAndGroupId(uuid, groupId);

        if (taskRecord != null) {
            deleteStagedModel(taskRecord);
        }
    }

    @Override
    public TaskRecord fetchStagedModelByUuidAndGroupId(String uuid, long groupId) {

        return _taskRecordLocalService.fetchTaskRecordByUuidAndGroupId(uuid, groupId);
    }

    @Override
    public List<TaskRecord> fetchStagedModelsByUuidAndCompanyId(String uuid, long companyId) {

        return _taskRecordLocalService.getTaskRecordsByUuidAndCompanyId(uuid, companyId, QueryUtil.ALL_POS,
                QueryUtil.ALL_POS, new StagedModelModifiedDateComparator<TaskRecord>());
    }

    @Override
    public String[] getClassNames() {
        return CLASS_NAMES;
    }

    @Override
    public String getDisplayName(TaskRecord taskRecord) {
        return String.valueOf(taskRecord.getTaskRecordId());
    }

    @Override
    protected void doExportStagedModel(PortletDataContext portletDataContext, TaskRecord taskRecord) throws Exception {

        Element taskRecordElement = portletDataContext.getExportDataElement(taskRecord);

        portletDataContext.addClassedModel(taskRecordElement, ExportImportPathUtil.getModelPath(taskRecord),
                taskRecord);
    }

    @Override
    protected void doImportMissingReference(PortletDataContext portletDataContext, String uuid, long groupId,
            long taskRecordId) throws Exception {

        TaskRecord existingTaskRecord = fetchMissingReference(uuid, groupId);

        if (existingTaskRecord == null) {
            return;
        }

        Map<Long, Long> taskRecordIds = (Map<Long, Long>) portletDataContext.getNewPrimaryKeysMap(TaskRecord.class);

        taskRecordIds.put(taskRecordId, existingTaskRecord.getTaskRecordId());
    }

    @Override
    protected void doImportStagedModel(PortletDataContext portletDataContext, TaskRecord taskRecord) throws Exception {

        long userId = portletDataContext.getUserId(taskRecord.getUserUuid());

        ServiceContext serviceContext = portletDataContext.createServiceContext(taskRecord);

        TaskRecord importedTaskRecord = null;

        if (portletDataContext.isDataStrategyMirror()) {

            TaskRecord existingTaskRecord = fetchStagedModelByUuidAndGroupId(taskRecord.getUuid(),
                    portletDataContext.getScopeGroupId());

            if (existingTaskRecord == null) {
                serviceContext.setUuid(taskRecord.getUuid());

                importedTaskRecord = _taskRecordLocalService.addTaskRecord(userId, taskRecord.getWorkPackage(),
                        taskRecord.getDescription(), taskRecord.getTicketURL(), taskRecord.getUntilDate(),
                        taskRecord.getFromDate(), taskRecord.getStatus(), taskRecord.getDuration(), serviceContext);

            } else {

                importedTaskRecord = _taskRecordLocalService.updateTaskRecord(userId,
                        existingTaskRecord.getTaskRecordId(), taskRecord.getWorkPackage(), taskRecord.getDescription(),
                        taskRecord.getTicketURL(), taskRecord.getUntilDate(), taskRecord.getFromDate(),
                        taskRecord.getStatus(), taskRecord.getDuration(), serviceContext);
            }
        } else {

            importedTaskRecord = _taskRecordLocalService.addTaskRecord(userId, taskRecord.getWorkPackage(),
                    taskRecord.getDescription(), taskRecord.getTicketURL(), taskRecord.getUntilDate(),
                    taskRecord.getFromDate(), taskRecord.getStatus(), taskRecord.getDuration(), serviceContext);
        }

        portletDataContext.importClassedModel(taskRecord, importedTaskRecord);
    }

    @Reference(unbind = "-")
    protected void setTaskRecordLocalService(TaskRecordLocalService taskRecordLocalService) {

        _taskRecordLocalService = taskRecordLocalService;
    }

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordStagedModelDataHandler.class);

    private TaskRecordLocalService _taskRecordLocalService;

}