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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.timetracker.constants.TaskRecordActionKeys;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.base.TaskRecordServiceBaseImpl;
import ch.inofix.timetracker.service.permission.TaskRecordPermission;
import ch.inofix.timetracker.service.permission.TimetrackerPortletPermission;

/**
 * The implementation of the task record remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.timetracker.service.TaskRecordService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have
 * security checks based on the propagated JAAS credentials because this service
 * can be accessed remotely.
 * </p>
 *
 * @author Christian Berndt, Stefan Luebbers
 * @created 2015-05-07 23:50
 * @modified 2017-03-23 10:40
 * @version 1.0.8
 * @see TaskRecordServiceBaseImpl
 * @see ch.inofix.timetracker.service.TaskRecordServiceUtil
 */
@ProviderType
public class TaskRecordServiceImpl extends TaskRecordServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Always use {@link
     * ch.inofix.timetracker.service.TaskRecordServiceUtil} to access the task
     * record remote service.
     */
    @Override
    public TaskRecord addTaskRecord(String workPackage, String description, String ticketURL, Date endDate,
            Date startDate, int status, long duration, ServiceContext serviceContext) throws PortalException {

        TimetrackerPortletPermission.check(getPermissionChecker(), serviceContext.getScopeGroupId(),
                TaskRecordActionKeys.ADD_TASK_RECORD);

        return taskRecordLocalService.addTaskRecord(getUserId(), workPackage, description, ticketURL, endDate,
                startDate, status, duration, serviceContext);

    }

    @Override
    public TaskRecord createTaskRecord() throws PortalException {

        // Create an empty taskRecord - no permission check required
        return taskRecordLocalService.createTaskRecord(0);
    }

    @Override
    public TaskRecord deleteTaskRecord(long taskRecordId) throws PortalException {

        TaskRecordPermission.check(getPermissionChecker(), taskRecordId, TaskRecordActionKeys.DELETE);

        return taskRecordLocalService.deleteTaskRecord(taskRecordId);

    }

    @Override
    public List<TaskRecord> deleteGroupTaskRecords(long groupId) throws PortalException {

        TimetrackerPortletPermission.check(getPermissionChecker(), groupId,
                TaskRecordActionKeys.DELETE_GROUP_TASK_RECORDS);

        return taskRecordLocalService.deleteGroupTaskRecords(groupId);
    }

    @Override
    public TaskRecord getTaskRecord(long taskRecordId) throws PortalException {
        TaskRecordPermission.check(getPermissionChecker(), taskRecordId, TaskRecordActionKeys.VIEW);
        return taskRecordLocalService.getTaskRecord(taskRecordId);
    }

    /**
     * looks deprecated - do we use it anywhere?
     */
    @Override
    @Deprecated
    public List<TaskRecord> getGroupTaskRecords(long groupId) throws PortalException {

        List<TaskRecord> taskRecordList = taskRecordLocalService.getGroupTaskRecords(groupId);
        for (TaskRecord taskRecord : taskRecordList) {
            TaskRecordPermission.check(getPermissionChecker(), taskRecord.getTaskRecordId(), TaskRecordActionKeys.VIEW);
        }

        return taskRecordList;
    }

    @Override
    public Hits search(long userId, long groupId, String keywords, int start, int end, Sort sort)
            throws PortalException{

//        try{
//        	TimetrackerPortletPermission.check(getPermissionChecker(), groupId, TaskRecordActionKeys.SEARCH);
//        }catch(Exception e){
//        	_log.error("No search permissions!" + e);
//        	return null;
//        }


        return taskRecordLocalService.search(userId, groupId, keywords, start, end, sort);
    }

    @Override
    public TaskRecord updateTaskRecord(long taskRecordId, String workPackage, String description, String ticketURL,
            Date endDate, Date startDate, int status, long duration, ServiceContext serviceContext)
            throws PortalException {

        TaskRecordPermission.check(getPermissionChecker(), taskRecordId, TaskRecordActionKeys.UPDATE);

        return taskRecordLocalService.updateTaskRecord(taskRecordId, getUserId(), workPackage, description, ticketURL,
                endDate, startDate, status, duration, serviceContext);

    }
    private static final Log _log = LogFactoryUtil.getLog(TaskRecordServiceImpl.class.getName());
}
