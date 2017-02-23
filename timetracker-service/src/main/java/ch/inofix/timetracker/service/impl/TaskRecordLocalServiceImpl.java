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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
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
 * @modified 2016-11-27 11:41
 * @version 1.5.2
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
    public TaskRecord addTaskRecord(long userId, long groupId, String workPackage, String description, String ticketURL,
            Date endDate, Date startDate, int status, long duration, ServiceContext serviceContext)
            throws PortalException, SystemException {

        TaskRecord taskRecord = saveTaskRecord(userId, groupId, 0, description, duration, endDate, startDate, status,
                workPackage, serviceContext);

        // Asset

        // TODO: re-enable asset framework
        //
        // resourceLocalService.addResources(taskRecord.getCompanyId(), groupId,
        // userId, TaskRecord.class.getName(),
        // taskRecord.getTaskRecordId(), false, true, true);
        //
        // updateAsset(userId, taskRecord, serviceContext.getAssetCategoryIds(),
        // serviceContext.getAssetTagNames(),
        // serviceContext.getAssetLinkEntryIds());

        return taskRecord;

    }

    /**
     * @param taskRecordId
     * @return
     */
    public TaskRecord deleteTaskRecord(long taskRecordId) throws PortalException, SystemException {

        TaskRecord taskRecord = taskRecordPersistence.findByPrimaryKey(taskRecordId);

        return deleteTaskRecord(taskRecord);
    }

    /**
     * 
     * @param groupId
     * @return
     * @since 1.5.2
     */
    public List<TaskRecord> getGroupTaskRecords(long groupId) throws PortalException, SystemException {

        return taskRecordPersistence.findByGroupId(groupId);
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
        searchContext.setEnd(end);
        searchContext.setGroupIds(new long[] { groupId });
        searchContext.setStart(start);
        searchContext.setUserId(userId);

        return indexer.search(searchContext);

    }

    public void updateAsset(long userId, TaskRecord taskRecord, long[] assetCategoryIds, String[] assetTagNames,
            long[] assetLinkEntryIds) throws PortalException, SystemException {

        boolean visible = true;
        // boolean visible = false;

        // if (taskRecord.isApproved()) {
        // visible = true;
        // }

        // TODO: What's the classTypeId?
        long classTypeId = 0;
        Date startDate = null;
        Date endDate = null;
        Date expirationDate = null;
        String mimeType = "text/text";
        String title = taskRecord.getWorkPackage();
        String description = taskRecord.getDescription();
        String summary = taskRecord.getDescription();
        // TODO: What does url mean in this context?
        String url = null;
        // TODO: What does layoutUuid mean in this context?
        String layoutUuid = null;
        int height = 0;
        int width = 0;
        Integer priority = null;
        boolean sync = false;

        AssetEntry assetEntry = assetEntryLocalService.updateEntry(userId, taskRecord.getGroupId(),
                taskRecord.getCreateDate(), taskRecord.getModifiedDate(), TaskRecord.class.getName(),
                taskRecord.getTaskRecordId(), taskRecord.getUuid(), classTypeId, assetCategoryIds, assetTagNames,
                visible, startDate, endDate, expirationDate, mimeType, title, description, summary, url, layoutUuid,
                height, width, priority, sync);

        // TODO
        // assetLinkLocalService.updateLinks(userId, assetEntry.getEntryId(),
        // assetLinkEntryIds,
        // AssetLinkConstants.TYPE_RELATED);

        // Indexer indexer =
        // IndexerRegistryUtil.nullSafeGetIndexer(TaskRecord.class);
        // indexer.reindex(taskRecord);
    }

    public TaskRecord updateTaskRecord(long userId, long groupId, long taskRecordId, String workPackage,
            String description, String ticketURL, Date endDate, Date startDate, int status, long duration,
            ServiceContext serviceContext) throws PortalException, SystemException {

        // TODO
        // Duration diff = new Duration(startDate.getTime(), endDate.getTime());
        //
        // if (duration == 0) {
        // duration = diff.getMillis();
        // }

        // TODO: Implement input validation.
        // either startDate / endDate or duration in milliseconds
        // workPackage: required
        // task: required

        TaskRecord taskRecord = saveTaskRecord(userId, groupId, taskRecordId, description, duration, endDate, startDate,
                status, workPackage, serviceContext);

        // Asset

        resourceLocalService.updateResources(serviceContext.getCompanyId(), serviceContext.getScopeGroupId(),
                taskRecord.getWorkPackage(), taskRecordId, serviceContext.getGroupPermissions(),
                serviceContext.getGuestPermissions());

        updateAsset(userId, taskRecord, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(),
                serviceContext.getAssetLinkEntryIds());

        return taskRecord;

    }

    private TaskRecord saveTaskRecord(long userId, long groupId, long taskRecordId, String description, long duration,
            Date endDate, Date startDate, int status, String workPackage, ServiceContext serviceContext)
            throws PortalException, SystemException {

        _log.info("add saveTaskRecord");

        User user = userPersistence.findByPrimaryKey(userId);
        Date now = new Date();
        TaskRecord taskRecord = null;

        // if (duration <= 0) {
        // duration = endDate.getTime() - startDate.getTime();
        // }

        if (taskRecordId > 0) {
            taskRecord = taskRecordLocalService.getTaskRecord(taskRecordId);
        } else {
            taskRecordId = counterLocalService.increment();
            taskRecord = taskRecordPersistence.create(taskRecordId);
            taskRecord.setCompanyId(user.getCompanyId());
            taskRecord.setGroupId(groupId);
            taskRecord.setUserId(user.getUserId());
            taskRecord.setUserName(user.getFullName());
            taskRecord.setCreateDate(now);
        }

        taskRecord.setModifiedDate(now);

        taskRecord.setDescription(description);
        taskRecord.setDuration(duration);
        taskRecord.setEndDate(endDate);
        taskRecord.setStartDate(startDate);
        taskRecord.setStatus(status);
        taskRecord.setWorkPackage(workPackage);
        taskRecord.setExpandoBridgeAttributes(serviceContext);

        taskRecordPersistence.update(taskRecord);

        return taskRecord;

    }

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordLocalServiceImpl.class.getName());
}