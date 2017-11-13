package ch.inofix.timetracker.web.internal.portlet.action;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordServiceUtil;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-11 00:11
 * @modified 2017-11-13 22:26
 * @version 1.0.1
 *
 */
public class ActionUtil {

    public static TaskRecord getTaskRecord(HttpServletRequest request) throws Exception {

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

    public static TaskRecord getTaskRecord(PortletRequest portletRequest) throws Exception {

        HttpServletRequest request = PortalUtil.getHttpServletRequest(portletRequest);

        return getTaskRecord(request);
    }

}
