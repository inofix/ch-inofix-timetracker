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

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;

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
 * @author Christian Berndt
 * @author Stefan Luebbers
 * @created 2015-05-07 23:50
 * @modified 2017-07-05 23:01
 * @version 1.1.7
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
    public FileEntry addTempFileEntry(long groupId, String folderName, String fileName, InputStream inputStream,
            String mimeType) throws PortalException {

        TimetrackerPortletPermission.check(getPermissionChecker(), groupId, TaskRecordActionKeys.IMPORT_TASK_RECORDS);

        return TempFileEntryUtil.addTempFileEntry(groupId, getUserId(),
                DigesterUtil.digestHex(Digester.SHA_256, folderName), fileName, inputStream, mimeType);
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
    public void deleteTempFileEntry(long groupId, String folderName, String fileName) throws PortalException {

        TimetrackerPortletPermission.check(getPermissionChecker(), groupId,
                TaskRecordActionKeys.EXPORT_IMPORT_TASK_RECORDS);

        TempFileEntryUtil.deleteTempFileEntry(groupId, getUserId(),
                DigesterUtil.digestHex(Digester.SHA_256, folderName), fileName);
    }

    @Override
    public List<TaskRecord> deleteGroupTaskRecords(long groupId) throws PortalException {

        TimetrackerPortletPermission.check(getPermissionChecker(), groupId,
                TaskRecordActionKeys.DELETE_GROUP_TASK_RECORDS);

        return taskRecordLocalService.deleteGroupTaskRecords(groupId);
    }

    @Override
    public long exportTaskRecordsAsFileInBackground(long userId, ExportImportConfiguration exportImportConfiguration)
            throws PortalException {

        TimetrackerPortletPermission.check(getPermissionChecker(), exportImportConfiguration.getGroupId(),
                TaskRecordActionKeys.EXPORT_TASK_RECORDS);

        return taskRecordLocalService.exportTaskRecordsAsFileInBackground(userId, exportImportConfiguration);

    }

    @Override
    public TaskRecord getTaskRecord(long taskRecordId) throws PortalException {

        TaskRecordPermission.check(getPermissionChecker(), taskRecordId, TaskRecordActionKeys.VIEW);
        return taskRecordLocalService.getTaskRecord(taskRecordId);
    }

    @Override
    public String[] getTempFileNames(long groupId, String folderName) throws PortalException {

        TimetrackerPortletPermission.check(getPermissionChecker(), groupId,
                TaskRecordActionKeys.EXPORT_IMPORT_TASK_RECORDS);

        return TempFileEntryUtil.getTempFileNames(groupId, getUserId(),
                DigesterUtil.digestHex(Digester.SHA_256, folderName));
    }

    @Override
    public long importTaskRecordsInBackground(ExportImportConfiguration exportImportConfiguration,
            InputStream inputStream) throws PortalException {

        Map<String, Serializable> settingsMap = exportImportConfiguration.getSettingsMap();

        long targetGroupId = MapUtil.getLong(settingsMap, "targetGroupId");

        // TODO: fix targetGroupId
        _log.info("targetGroupId = " + targetGroupId);

        TimetrackerPortletPermission.check(getPermissionChecker(), targetGroupId,
                TaskRecordActionKeys.IMPORT_TASK_RECORDS);

        return taskRecordLocalService.importTaskRecordsInBackground(getUserId(), exportImportConfiguration,
                inputStream);
    }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String keywords, int start, int end, Sort sort)
            throws PortalException {

        return taskRecordLocalService.search(userId, groupId, ownerUserId, keywords, start, end, sort);
    }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String workPackage, String description, int status,
            Date fromDate, Date untilDate, LinkedHashMap<String, Object> params, boolean andSearch, int start, int end,
            Sort sort) throws PortalException {

        return taskRecordLocalService.search(userId, groupId, ownerUserId, workPackage, description, status, fromDate,
                untilDate, params, andSearch, start, end, sort);

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
