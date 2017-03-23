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
        	_log.info("[DEBUGGING]check failed");
            throw new PrincipalException();
        }
    }

    public static void check(PermissionChecker permissionChecker, long taskRecordId, String actionId)
            throws PortalException {
    	_log.info("[DEBUGGING]call contains with id:" + taskRecordId);
        if (!contains(permissionChecker, taskRecordId, actionId)) {
        	_log.info("[DEBUGGING]check failed - " + taskRecordId);
            throw new PrincipalException();
        }
        _log.info("[DEBUGGING]successfully checked id:" + taskRecordId);
    }

    public static boolean contains(PermissionChecker permissionChecker, TaskRecord taskRecord, String actionId) {
    	_log.info("[DEBUGGING]call hasOwnerPermission.");
        if (permissionChecker.hasOwnerPermission(taskRecord.getCompanyId(), TaskRecord.class.getName(),
                taskRecord.getTaskRecordId(), taskRecord.getUserId(), actionId)) {
        	_log.info("[DEBUGGING]successfully checked (OwnerPermission) taskRecord:" + taskRecord.getTaskRecordId());
            return true;
        }
        _log.info("[DEBUGGING]call hasPermission.");
        _log.info("[DEBUGGING]groupID: " + taskRecord.getGroupId() + " - Name: " + TaskRecord.class.getName() +
        		" - TaskRecordId: " + taskRecord.getTaskRecordId() + " - actionId: " + actionId);
        _log.info("[DEBUGGING]permissionchecker companyId: " +permissionChecker.getCompanyId() +
        		"  userid: " + permissionChecker.getUserId() +
        		"  groupid: " + permissionChecker.getUser().getGroupId());
        
        return permissionChecker.hasPermission(taskRecord.getGroupId(), TaskRecord.class.getName(),
                String.valueOf(taskRecord.getTaskRecordId()), actionId);
    }

    public static boolean contains(PermissionChecker permissionChecker, long taskRecordId, String actionId){
    	_log.info("[DEBUGGING]getTaskRecord from LocalService:" + taskRecordId);
        
    	TaskRecord taskRecord;
		try {
			taskRecord = TaskRecordLocalServiceUtil.getTaskRecord(taskRecordId);
			_log.info("[DEBUGGING]call contains with TaskRecord:" + taskRecordId);
			return contains(permissionChecker, taskRecord, actionId);
		} catch (PortalException e) {
			_log.error(e);
		}
        
        return false;
    }
    
    private static final Log _log = LogFactoryUtil.getLog(TaskRecordPermission.class.getName());

}
