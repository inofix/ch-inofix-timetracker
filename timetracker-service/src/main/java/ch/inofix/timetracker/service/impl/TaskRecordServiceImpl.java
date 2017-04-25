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

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

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
 * @author Christian Berndt, Stefan Luebbers
 * @created 2015-05-07 23:50
 * @modified 2017-04-09 22:50
 * @version 1.1.1
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

        _log.info("addTempFileEntry()");

        // TODO
        // GroupPermissionUtil.check(getPermissionChecker(), groupId,
        // TaskRecordActionKeys.IMPORT_TASK_RECORDS);

        // try {
        //
        // FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(groupId,
        // getUserId(),
        // ExportImportHelper.TEMP_FOLDER_NAME, fileName, inputStream,
        // mimeType);
        //
        // _log.info("fileEntry = " + fileEntry);
        //
        // return fileEntry;
        //
        // } catch (Exception e) {
        // _log.error(e);
        // throw new PortalException(e);
        // }

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

        // TODO
        // GroupPermissionUtil.check(getPermissionChecker(), groupId,
        // TaskRecordActionKeys.IMPORT_TASK_RECORDS);

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

        // TODO: enable permission check
//        TimetrackerPortletPermission.check(getPermissionChecker(), exportImportConfiguration.getGroupId(),
//                TaskRecordActionKeys.EXPORT_TASK_RECORDS);

        return taskRecordLocalService.exportTaskRecordsAsFileInBackground(userId, exportImportConfiguration);

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
    public TaskRecord getTaskRecord(long taskRecordId) throws PortalException {

        TaskRecordPermission.check(getPermissionChecker(), taskRecordId, TaskRecordActionKeys.VIEW);
        return taskRecordLocalService.getTaskRecord(taskRecordId);
    }

    @Override
    public String[] getTempFileNames(long groupId, String folderName) throws PortalException {

        // TODO
        // GroupPermissionUtil.check(getPermissionChecker(), groupId, TaskRecordActionKeys.IMPORT_TASK_RECORDS);

        return TempFileEntryUtil.getTempFileNames(groupId, getUserId(),
                DigesterUtil.digestHex(Digester.SHA_256, folderName));
    }

    @Override
    public long importTaskRecordsInBackground(File file)
            throws PortalException {

        _log.info("importTaskRecordsInBackground()");
//
//        Map<String, Serializable> settingsMap = taskRecordConfiguration.getSettingsMap();
//
//        long targetGroupId = MapUtil.getLong(settingsMap, "targetGroupId");

        // TODO
        // GroupPermissionUtil.check(
        // getPermissionChecker(), targetGroupId,
        // TaskRecordActionKeys.IMPORT_TASK_RECORDS);

        return taskRecordLocalService.importTaskRecordsInBackground(getUserId(), file);
    }

    @Override
    public long importTaskRecordsInBackground(
            InputStream inputStream) throws PortalException {

        _log.info("importTaskRecordsInBackground()");

//        Map<String, Serializable> settingsMap = taskRecordConfiguration.getSettingsMap();

//        long targetGroupId = MapUtil.getLong(settingsMap, "targetGroupId");

        // TODO
        // GroupPermissionUtil.check(
        // getPermissionChecker(), targetGroupId,
        // TaskRecordActionKeys.IMPORT_TASK_RECORDS);

        return taskRecordLocalService.importTaskRecordsInBackground(getUserId(), inputStream);
    }

    @Override
    public Hits search(long userId, long groupId, String keywords, int start, int end, Sort sort)
            throws PortalException {

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
