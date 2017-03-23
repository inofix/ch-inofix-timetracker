/**
 * Copyright (c) 2000-present Inofix GmbH, Luzern. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package ch.inofix.timetracker.service.impl;

import java.util.Date;
import java.util.List;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetLinkConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.base.TaskRecordLocalServiceBaseImpl;

/**
 * The implementation of the task record local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.timetracker.service.TaskRecordLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author Christian Berndt
 * @created 2013-10-06 21:24
 * @modified 2017-03-23 10:37
 * @version 1.5.4
 * @see TaskRecordLocalServiceBaseImpl
 * @see ch.inofix.timetracker.service.TaskRecordLocalServiceUtil
 */
@ProviderType
public class TaskRecordLocalServiceImpl extends TaskRecordLocalServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Always use {@link
     * ch.inofix.timetracker.service.TaskRecordLocalServiceUtil} to access the
     * task record local service.
     */
    @Override
    @Indexable(type = IndexableType.REINDEX)
    public TaskRecord addTaskRecord(long userId, String workPackage, String description, String ticketURL, Date endDate,
            Date startDate, int status, long duration, ServiceContext serviceContext) throws PortalException {

        // TaskRecord

        User user = userPersistence.findByPrimaryKey(userId);
        long groupId = serviceContext.getScopeGroupId();

        // TODO
        // validate(groupId, urlTitle);

        long taskRecordId = counterLocalService.increment();

        TaskRecord taskRecord = taskRecordPersistence.create(taskRecordId);

        taskRecord.setUuid(serviceContext.getUuid());
        taskRecord.setGroupId(groupId);
        taskRecord.setCompanyId(user.getCompanyId());
        taskRecord.setUserId(user.getUserId());
        taskRecord.setUserName(user.getFullName());
        taskRecord.setExpandoBridgeAttributes(serviceContext);

        taskRecord.setWorkPackage(workPackage);
        taskRecord.setDescription(description);
        taskRecord.setTicketURL(ticketURL);
        taskRecord.setEndDate(endDate);
        taskRecord.setStartDate(startDate);
        taskRecord.setStatus(status);
        taskRecord.setDuration(duration);

        taskRecord = taskRecordPersistence.update(taskRecord);

        // Resources

        if (serviceContext.isAddGroupPermissions() || serviceContext.isAddGuestPermissions()) {
            addTaskRecordResources(taskRecord, serviceContext.isAddGroupPermissions(),
                    serviceContext.isAddGuestPermissions());
        } else {
            addTaskRecordResources(taskRecord, serviceContext.getModelPermissions());
        }

        // Asset

        updateAsset(userId, taskRecord, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(),
                serviceContext.getAssetLinkEntryIds(), serviceContext.getAssetPriority());

        return taskRecord;

    }

    @Override
    public void addTaskRecordResources(TaskRecord taskRecord, boolean addGroupPermissions, boolean addGuestPermissions)
            throws PortalException {

        resourceLocalService.addResources(taskRecord.getCompanyId(), taskRecord.getGroupId(), taskRecord.getUserId(),
                TaskRecord.class.getName(), taskRecord.getTaskRecordId(), false, addGroupPermissions,
                addGuestPermissions);
    }

    @Override
    public void addTaskRecordResources(TaskRecord taskRecord, ModelPermissions modelPermissions)
            throws PortalException {

        resourceLocalService.addModelResources(taskRecord.getCompanyId(), taskRecord.getGroupId(),
                taskRecord.getUserId(), TaskRecord.class.getName(), taskRecord.getTaskRecordId(), modelPermissions);
    }

    @Override
    public void addTaskRecordResources(long taskRecordId, boolean addGroupPermissions, boolean addGuestPermissions)
            throws PortalException {

        TaskRecord taskRecord = taskRecordPersistence.findByPrimaryKey(taskRecordId);

        addTaskRecordResources(taskRecord, addGroupPermissions, addGuestPermissions);
    }

    @Override
    public void addTaskRecordResources(long taskRecordId, ModelPermissions modelPermissions) throws PortalException {

        TaskRecord taskRecord = taskRecordPersistence.findByPrimaryKey(taskRecordId);

        addTaskRecordResources(taskRecord, modelPermissions);
    }

    /**
    *
    * @param groupId
    * @return
    * @since 1.5.2
    */
   @Override
   public List<TaskRecord> deleteGroupTaskRecords(long groupId) throws PortalException {

       List<TaskRecord> taskRecords = taskRecordPersistence.findByGroupId(groupId);

       for (TaskRecord taskRecord : taskRecords) {

           //TODO differ exception types
    	   try {
               deleteTaskRecord(taskRecord);
           } catch (Exception e) { 
               _log.error(e);
           }
       }

       return taskRecords;

   }

    @Indexable(type = IndexableType.DELETE)
    @Override
    @SystemEvent(type = SystemEventConstants.TYPE_DELETE)
    public TaskRecord deleteTaskRecord(TaskRecord taskRecord) throws PortalException {

        // TaskRecord

        taskRecordPersistence.remove(taskRecord);

        // Resources

        resourceLocalService.deleteResource(taskRecord.getCompanyId(), TaskRecord.class.getName(),
                ResourceConstants.SCOPE_INDIVIDUAL, taskRecord.getTaskRecordId());

        // Asset

        assetEntryLocalService.deleteEntry(TaskRecord.class.getName(), taskRecord.getTaskRecordId());

        // Workflow

        // TODO: add workflow support
        // workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
        // taskRecord.getCompanyId(), taskRecord.getGroupId(),
        // TaskRecord.class.getName(), taskRecord.getTaskRecordId());

        return taskRecord;
    }

    @Override
    public TaskRecord deleteTaskRecord(long taskRecordId) throws PortalException {
        TaskRecord taskRecord = taskRecordPersistence.findByPrimaryKey(taskRecordId);

        return taskRecordLocalService.deleteTaskRecord(taskRecord);
    }

    /**
     *
     * @param groupId
     * @return
     * @since 1.5.2
     */
    @Override
    public List<TaskRecord> getGroupTaskRecords(long groupId) throws PortalException {

        List<TaskRecord> taskRecords = taskRecordPersistence.findByGroupId(groupId);

        return taskRecords;

    }

    @Override
    public Hits search(long userId, long groupId, String keywords, int start, int end, Sort sort)
            throws PortalException {

        if (sort == null) {
            sort = new Sort(Field.MODIFIED_DATE, true);
        }

        Indexer<TaskRecord> indexer = IndexerRegistryUtil.getIndexer(TaskRecord.class.getName());

        SearchContext searchContext = new SearchContext();

        searchContext.setAttribute(Field.STATUS, WorkflowConstants.STATUS_ANY);

        searchContext.setAttribute("paginationType", "more");

        Group group = GroupLocalServiceUtil.getGroup(groupId);

        searchContext.setCompanyId(group.getCompanyId());

        searchContext.setEnd(end);
        searchContext.setGroupIds(new long[] { groupId });
        searchContext.setSorts(sort);
        searchContext.setStart(start);
//        searchContext.setEnd(end);
//        searchContext.setGroupIds(new long[] { groupId });
//        searchContext.setStart(start);
//        searchContext.setUserId(userId);

        return indexer.search(searchContext);

    }

    @Override
    public void updateAsset(long userId, TaskRecord taskRecord, long[] assetCategoryIds, String[] assetTagNames,
            long[] assetLinkEntryIds, Double priority) throws PortalException {

        // TODO
        boolean visible = true;
        // boolean visible = false;
        // if (taskRecord.isApproved()) {
        // visible = true;
        // publishDate = taskRecord.getCreateDate();
        // }

        Date publishDate = null;

        String summary = HtmlUtil.extractText(StringUtil.shorten(taskRecord.getWorkPackage(), 500));

        String className = TaskRecord.class.getName();
        long classPK = taskRecord.getTaskRecordId();

        AssetEntry assetEntry = assetEntryLocalService.updateEntry(userId, taskRecord.getGroupId(),
                taskRecord.getCreateDate(), taskRecord.getModifiedDate(), className, classPK, taskRecord.getUuid(), 0,
                assetCategoryIds, assetTagNames, true, visible, null, null, publishDate, null, ContentTypes.TEXT_HTML,
                taskRecord.getWorkPackage(), taskRecord.getWorkPackage(), summary, null, null, 0, 0, priority);

        assetLinkLocalService.updateLinks(userId, assetEntry.getEntryId(), assetLinkEntryIds,
                AssetLinkConstants.TYPE_RELATED);
    }

    @Override
    @Indexable(type = IndexableType.REINDEX)
    public TaskRecord updateTaskRecord(long taskRecordId, long userId, String workPackage, String description,
            String ticketURL, Date endDate, Date startDate, int status, long duration, ServiceContext serviceContext)
            throws PortalException {

        // TaskRecord

        User user = userPersistence.findByPrimaryKey(userId);

        TaskRecord taskRecord = taskRecordPersistence.findByPrimaryKey(taskRecordId);

        long groupId = serviceContext.getScopeGroupId();

        // TODO: validate taskRecord

        taskRecord.setUuid(serviceContext.getUuid());
        taskRecord.setGroupId(groupId);
        taskRecord.setCompanyId(user.getCompanyId());
        taskRecord.setUserId(user.getUserId());
        taskRecord.setUserName(user.getFullName());
        taskRecord.setExpandoBridgeAttributes(serviceContext);

        taskRecord.setWorkPackage(workPackage);
        taskRecord.setDescription(description);
        taskRecord.setTicketURL(ticketURL);
        taskRecord.setEndDate(endDate);
        taskRecord.setStartDate(startDate);
        taskRecord.setStatus(status);
        taskRecord.setDuration(duration);

        taskRecordPersistence.update(taskRecord);

        // Resources

        resourceLocalService.addModelResources(taskRecord, serviceContext);

        // Asset

        updateAsset(userId, taskRecord, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(),
                serviceContext.getAssetLinkEntryIds(), serviceContext.getAssetPriority());

        return taskRecord;

    }

    @Override
    public void updateTaskRecordResources(TaskRecord taskRecord, ModelPermissions modelPermissions)
            throws PortalException {

        resourceLocalService.updateResources(taskRecord.getCompanyId(), taskRecord.getGroupId(),
                TaskRecord.class.getName(), taskRecord.getTaskRecordId(), modelPermissions);
    }

    @Override
    public void updateTaskRecordResources(TaskRecord taskRecord, String[] groupPermissions, String[] guestPermissions)
            throws PortalException {

        resourceLocalService.updateResources(taskRecord.getCompanyId(), taskRecord.getGroupId(),
                TaskRecord.class.getName(), taskRecord.getTaskRecordId(), groupPermissions, guestPermissions);
    }

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordLocalServiceImpl.class.getName());
}