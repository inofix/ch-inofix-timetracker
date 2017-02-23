package ch.inofix.timetracker.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.BaseResourcePermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import ch.inofix.timetracker.constants.TimetrackerPortletKeys;

/**
 * 
 * @author Christian Berndt
 * @created 2016-11-13 18:09
 * @modified 2016-11-13 18:09
 * @version 1.0.0
 *
 */
public class TimetrackerPortletPermission extends BaseResourcePermissionChecker {

    public static final String RESOURCE_NAME = "ch.inofix.timetracker";

    public static void check(PermissionChecker permissionChecker, long groupId, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, groupId, actionId)) {
            throw new PrincipalException();
        }
    }

    public static boolean contains(PermissionChecker permissionChecker, long groupId, String actionId) {

        return contains(permissionChecker, RESOURCE_NAME, TimetrackerPortletKeys.TIMETRACKER, groupId, actionId);
    }

    @Override
    public Boolean checkResource(PermissionChecker permissionChecker, long classPK, String actionId) {

        return contains(permissionChecker, classPK, actionId);
    }

}
