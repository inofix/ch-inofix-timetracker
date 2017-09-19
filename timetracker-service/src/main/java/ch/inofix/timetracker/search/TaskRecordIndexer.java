package ch.inofix.timetracker.search;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
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
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalService;
import ch.inofix.timetracker.service.permission.TaskRecordPermission;

/**
 *
 * @author Christian Berndt
 * @author Stefan Luebbers
 * @created 2016-11-26 15:04
 * @modified 2017-09-19 21:09
 * @version 1.1.2
 *
 */
@Component(immediate = true, service = Indexer.class)
public class TaskRecordIndexer extends BaseIndexer<TaskRecord> {

    public static final String CLASS_NAME = TaskRecord.class.getName();

    public TaskRecordIndexer() {
        setDefaultSelectedFieldNames(Field.ASSET_TAG_NAMES, Field.COMPANY_ID, Field.ENTRY_CLASS_NAME,
                Field.ENTRY_CLASS_PK, Field.GROUP_ID, Field.MODIFIED_DATE, Field.SCOPE_GROUP_ID, Field.TITLE, Field.UID,
                "workPackage");
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
    public void postProcessContextBooleanFilter(BooleanFilter contextBooleanFilter, SearchContext searchContext)
            throws Exception {

        addStatus(contextBooleanFilter, searchContext);

        // from- and until-date

        Date fromDate = GetterUtil.getDate(searchContext.getAttribute("fromDate"), DateFormat.getDateInstance(), null);
        Date untilDate = GetterUtil.getDate(searchContext.getAttribute("untilDate"), DateFormat.getDateInstance(),
                null);

        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;

        if (fromDate != null) {
            min = fromDate.getTime();
        }

        if (untilDate != null) {
            max = untilDate.getTime();
        }

        contextBooleanFilter.addRangeTerm("fromDate_Number_sortable", min, max);

    }

    @Override
    public void postProcessSearchQuery(BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
            SearchContext searchContext) throws Exception {
        
        boolean advancedSearch = GetterUtil.getBoolean(searchContext.getAttribute("advancedSearch"));

        addSearchTerm(searchQuery, searchContext, "description", false);
        if (!advancedSearch) {
            addSearchTerm(searchQuery, searchContext, "workPackage", true);
        }
        
        // TODO: add ticketURL

        LinkedHashMap<String, Object> params = (LinkedHashMap<String, Object>) searchContext.getAttribute("params");

        if (params != null) {
            String expandoAttributes = (String) params.get("expandoAttributes");

            if (Validator.isNotNull(expandoAttributes)) {
                addSearchExpando(searchQuery, searchContext, expandoAttributes);
            }
        }
    }

    @Override
    protected void doDelete(TaskRecord taskRecord) throws Exception {
        deleteDocument(taskRecord.getCompanyId(), taskRecord.getTaskRecordId());
    }

    @Override
    protected Document doGetDocument(TaskRecord taskRecord) throws Exception {

        Document document = getBaseModelDocument(CLASS_NAME, taskRecord);

        document.addDateSortable(Field.CREATE_DATE, taskRecord.getCreateDate());
        document.addTextSortable("description", taskRecord.getDescription());
        document.addNumberSortable("duration", taskRecord.getDuration());
        document.addDateSortable("fromDate", taskRecord.getFromDate());
        document.addNumberSortable("taskRecordId", taskRecord.getTaskRecordId());
        document.addNumberSortable(Field.STATUS, taskRecord.getStatus());
        document.addTextSortable("ticketURL", taskRecord.getTicketURL());
        document.addKeyword("ownerUserId", taskRecord.getUserId());
        document.addDateSortable("modifiedDate", taskRecord.getModifiedDate());
        document.addDateSortable("untilDate", taskRecord.getUntilDate());
        document.addTextSortable("userName", taskRecord.getUserName());
        document.addTextSortable("workPackage", taskRecord.getWorkPackage());

        return document;

    }

    @Override
    protected Summary doGetSummary(Document document, Locale locale, String snippet, PortletRequest portletRequest,
            PortletResponse portletResponse) throws Exception {

        Summary summary = createSummary(document, Field.TITLE, Field.CONTENT);

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
        reindexTaskRecords(companyId);
    }

    @Override
    protected void doReindex(TaskRecord taskRecord) throws Exception {

        Document document = getDocument(taskRecord);

        IndexWriterHelperUtil.updateDocument(getSearchEngineId(), taskRecord.getCompanyId(), document,
                isCommitImmediately());
    }
    
    @Override 
    protected void postProcessFullQuery(BooleanQuery fullQuery, SearchContext searchContext) {

        String workPackage = (String) searchContext.getAttribute("workPackage");

        if (Validator.isNotNull(workPackage)) {       
            
            fullQuery.addRequiredTerm("workPackage_sortable", workPackage);
        }
        
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
