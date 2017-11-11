package ch.inofix.timetracker.web.internal.portlet.action;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordServiceUtil;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-11 00:11
 * @modified 2017-11-11 00:11
 * @version 1.0.0
 *
 */
public class ActionUtil {
    

    public static TaskRecord getTaskRecord(HttpServletRequest request) throws Exception {
        
        _log.info("getTaskRecord(request)");

        long taskRecordId = ParamUtil.getLong(request, "taskRecordId");

        TaskRecord taskRecord = null;

        if (taskRecordId > 0) {
            taskRecord = TaskRecordServiceUtil.getTaskRecord(taskRecordId);

            // TODO: Add TrashBin support
            // if (taskRecord.isInTrash()) {
            // throw new NoSuchTaskRecordException("{taskRecordId=" +
            // taskRecordId + "}");
            // }
        }

        return taskRecord;
    }

    public static TaskRecord getTaskRecord(PortletRequest portletRequest)
        throws Exception {

        _log.info("getTaskRecord(portletRequest)");

        HttpServletRequest request = PortalUtil.getHttpServletRequest(
            portletRequest);

        return getTaskRecord(request);
    }
    
    private static Log _log = LogFactoryUtil.getLog(ActionUtil.class.getName()); 


}
