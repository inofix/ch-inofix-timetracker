package ch.inofix.timetracker.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.BaseResourcePermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import ch.inofix.timetracker.constants.PortletKeys;

/**
 * 
 * @author Christian Berndt
 * @created 2016-11-13 18:09
 * @modified 2017-03-21 14:56
 * @version 1.0.1
 *
 */
public class TimetrackerPortletPermission extends BaseResourcePermissionChecker {

    public static final String RESOURCE_NAME = "ch.inofix.timetracker";

    public static void check(PermissionChecker permissionChecker, long groupId, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, groupId, actionId)) {
        	_log.info("[DEBUGGING]check failed- PrincipalException - groupid:" + groupId + " actionKey: "+actionId);
            throw new PrincipalException();
        }
        _log.info("[DEBUGGING]successfully checked groupid:" + groupId + " actionKey: "+actionId);
    }

    public static boolean contains(PermissionChecker permissionChecker, long groupId, String actionId) {
    	_log.info("[DEBUGGING]groupid:" + groupId + " contains "+
    					contains(permissionChecker, RESOURCE_NAME, PortletKeys.TIMETRACKER, groupId, actionId)
    					+" actionKey: "+actionId);
        return contains(permissionChecker, RESOURCE_NAME, PortletKeys.TIMETRACKER, groupId, actionId);
    }

    @Override
    public Boolean checkResource(PermissionChecker permissionChecker, long classPK, String actionId) {
    	_log.info("[DEBUGGING]classPK:" + classPK + " contains "+
    					contains(permissionChecker, classPK, actionId)
    					+" actionKey: "+actionId);
        return contains(permissionChecker, classPK, actionId);
    }
    
    private static final Log _log = LogFactoryUtil.getLog(TimetrackerPortletPermission.class.getName());

}
