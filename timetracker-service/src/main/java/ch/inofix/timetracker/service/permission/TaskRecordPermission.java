package ch.inofix.timetracker.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.BaseResourcePermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalServiceUtil;
import ch.inofix.timetracker.service.impl.TaskRecordLocalServiceImpl;

/**
 *
 * @author Christian Berndt
 * @created 2016-11-13 17:55
 * @modified 2017-03-22 12:40
 * @version 1.0.2
 *
 */
public class TaskRecordPermission{

    public static void check(PermissionChecker permissionChecker, TaskRecord taskRecord, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, taskRecord, actionId)) {
            throw new PrincipalException();
        }
    }

    public static void check(PermissionChecker permissionChecker, long taskRecordId, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, taskRecordId, actionId)) {
            throw new PrincipalException();
        }
    }

    public static boolean contains(PermissionChecker permissionChecker, TaskRecord taskRecord, String actionId) {

        if (permissionChecker.hasOwnerPermission(taskRecord.getCompanyId(), TaskRecord.class.getName(),
                taskRecord.getTaskRecordId(), taskRecord.getUserId(), actionId)) {

            return true;
        }

        return permissionChecker.hasPermission(taskRecord.getGroupId(), TaskRecord.class.getName(),
                String.valueOf(taskRecord.getTaskRecordId()), actionId);
    }

    public static boolean contains(PermissionChecker permissionChecker, long taskRecordId, String actionId){

    	TaskRecord taskRecord;
        try {
            taskRecord = TaskRecordLocalServiceUtil.getTaskRecord(taskRecordId);
            return contains(permissionChecker, taskRecord, actionId);
        } catch (PortalException e) {
            _log.error(e);
        }
        
        return false;
    }
    
    private static final Log _log = LogFactoryUtil.getLog(TaskRecordPermission.class.getName());

}
