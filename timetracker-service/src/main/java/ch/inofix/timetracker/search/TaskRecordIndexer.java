package ch.inofix.timetracker.search;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalService;
import ch.inofix.timetracker.service.permission.TaskRecordPermission;

/**
 * 
 * @author Christian Berndt
 * @created 2016-11-26 15:04
 * @modified 2016-11-26 15:04
 * @version 1.0.0
 *
 */
@Component(immediate = true, service = Indexer.class)
public class TaskRecordIndexer extends BaseIndexer<TaskRecord> {

    public static final String CLASS_NAME = TaskRecord.class.getName();

    public TaskRecordIndexer() {
        setDefaultSelectedFieldNames(Field.ASSET_TAG_NAMES, "author", Field.COMPANY_ID, Field.ENTRY_CLASS_NAME,
                Field.ENTRY_CLASS_PK, Field.GROUP_ID, Field.MODIFIED_DATE, Field.SCOPE_GROUP_ID, Field.TITLE, Field.UID,
                Field.URL, "year");
        setFilterSearch(true);
        setPermissionAware(true);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    public boolean hasPermission(PermissionChecker permissionChecker, String entryClassName, long entryClassPK,
            String actionId) throws Exception {
        return TaskRecordPermission.contains(permissionChecker, entryClassPK, ActionKeys.VIEW);
    }

    @Override
    protected void doDelete(TaskRecord taskRecord) throws Exception {

        deleteDocument(taskRecord.getCompanyId(), taskRecord.getTaskRecordId());
    }

    @Override
    protected Document doGetDocument(TaskRecord taskRecord) throws Exception {

        Document document = getBaseModelDocument(CLASS_NAME, taskRecord);

        // TODO: add required fields to document
        document.addText(Field.CONTENT, taskRecord.getDescription());
        document.addTextSortable(Field.TITLE, taskRecord.getWorkPackage());
        document.addTextSortable("workPackage", taskRecord.getWorkPackage());

        return document;

    }

    @Override
    protected Summary doGetSummary(Document document, Locale locale, String snippet, PortletRequest portletRequest,
            PortletResponse portletResponse) throws Exception {

        Summary summary = createSummary(document, Field.TITLE, Field.URL);

        return summary;
    }

    @Override
    protected void doReindex(String className, long classPK) throws Exception {
        TaskRecord taskRecord = _taskRecordLocalService.getTaskRecord(classPK);

        doReindex(taskRecord);
    }

    @Override
    protected void doReindex(String[] ids) throws Exception {
        long companyId = GetterUtil.getLong(ids[0]);

        // TODO: what about the group?
        reindexTaskRecords(companyId);
        // reindexTaskRecords(companyId, groupId);

    }

    @Override
    protected void doReindex(TaskRecord reference) throws Exception {

        Document document = getDocument(reference);

        IndexWriterHelperUtil.updateDocument(getSearchEngineId(), reference.getCompanyId(), document,
                isCommitImmediately());
    }

    protected void reindexTaskRecords(long companyId) throws PortalException {

        final IndexableActionableDynamicQuery indexableActionableDynamicQuery = _taskRecordLocalService
                .getIndexableActionableDynamicQuery();

        indexableActionableDynamicQuery.setAddCriteriaMethod(new ActionableDynamicQuery.AddCriteriaMethod() {

            @Override
            public void addCriteria(DynamicQuery dynamicQuery) {

                Property statusProperty = PropertyFactoryUtil.forName("status");

                Integer[] statuses = { WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_IN_TRASH };

                dynamicQuery.add(statusProperty.in(statuses));
            }

        });
        indexableActionableDynamicQuery.setCompanyId(companyId);
        // TODO: what about the group?
        // indexableActionableDynamicQuery.setGroupId(groupId);
        indexableActionableDynamicQuery
                .setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<TaskRecord>() {

                    @Override
                    public void performAction(TaskRecord taskRecord) {
                        try {
                            Document document = getDocument(taskRecord);

                            indexableActionableDynamicQuery.addDocuments(document);
                        } catch (PortalException pe) {
                            if (_log.isWarnEnabled()) {
                                _log.warn("Unable to index taskRecord " + taskRecord.getTaskRecordId(), pe);
                            }
                        }
                    }

                });
        indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());

        indexableActionableDynamicQuery.performActions();
    }

    @Reference(unbind = "-")
    protected void setTaskRecordLocalService(TaskRecordLocalService taskRecordLocalService) {

        _taskRecordLocalService = taskRecordLocalService;
    }

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordIndexer.class);

    private TaskRecordLocalService _taskRecordLocalService;
}
