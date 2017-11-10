package ch.inofix.timetracker.web.internal.portlet.action;

import java.util.Date;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.exception.NoSuchTaskRecordException;
import ch.inofix.timetracker.exception.TaskRecordFromDateException;
import ch.inofix.timetracker.exception.TaskRecordUntilDateException;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordService;
import ch.inofix.timetracker.web.internal.constants.TimetrackerWebKeys;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-10 19:09
 * @modified 2017-11-10 19:09
 * @version 1.0.0
 *
 */
@Component(
    property = {
        "javax.portlet.name=" + PortletKeys.TIMETRACKER,
        "mvc.command.name=editTaskRecord"
    },
    service = MVCActionCommand.class
)
public class EditTaskRecordMVCActionCommand extends BaseMVCActionCommand {
    
    protected void deleteGroupTaskRecords(ActionRequest actionRequest) throws Exception {

        _log.info("deleteGroupTaskRecords()");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(TaskRecord.class.getName(), actionRequest);

        _taskRecordService.deleteGroupTaskRecords(serviceContext.getScopeGroupId());

    }

    protected void deleteTaskRecords(ActionRequest actionRequest) throws Exception {

        long taskRecordId = ParamUtil.getLong(actionRequest, "taskRecordId");

        long[] taskRecordIds = ParamUtil.getLongValues(actionRequest, "deleteTaskRecordIds");

        if (taskRecordId > 0) {
            taskRecordIds = new long[] { taskRecordId };
        }

        for (long id : taskRecordIds) {
            _taskRecordService.deleteTaskRecord(id);
        }

    }

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

        try {

            if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
                updateTaskRecord(actionRequest);
            } else if (cmd.equals(Constants.DELETE)) {
                deleteTaskRecords(actionRequest);
            } else if (cmd.equals("deleteGroupTaskRecords")) {
                deleteGroupTaskRecords(actionRequest);
            }

        } catch (NoSuchTaskRecordException | PrincipalException e) {

            SessionErrors.add(actionRequest, e.getClass());

            actionResponse.setRenderParameter("mvcPath", "/error.jsp");

            // TODO: Define set of exceptions reported back to user. For an
            // example, see EditCategoryMVCActionCommand.java.

        } catch (Exception e) {

            SessionErrors.add(actionRequest, e.getClass());
        }
    }

    @Reference(unbind = "-")
    protected void setTaskRecordService(TaskRecordService taskRecordService) {
        this._taskRecordService = taskRecordService;
    }

    protected void updateTaskRecord(ActionRequest actionRequest) throws Exception {

        long taskRecordId = ParamUtil.getLong(actionRequest, "taskRecordId");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(TaskRecord.class.getName(), actionRequest);

        String workPackage = ParamUtil.getString(actionRequest, "workPackage");
        String description = ParamUtil.getString(actionRequest, "description");
        String ticketURL = ParamUtil.getString(actionRequest, "ticketURL");
        int durationInMinutes = ParamUtil.getInteger(actionRequest, "duration");
        long duration = durationInMinutes * 60 * 1000;
        int status = ParamUtil.getInteger(actionRequest, "status");

        int fromDateDay = ParamUtil.getInteger(actionRequest, "fromDateDay");
        int fromDateMonth = ParamUtil.getInteger(actionRequest, "fromDateMonth");
        int fromDateYear = ParamUtil.getInteger(actionRequest, "fromDateYear");
        int fromDateHour = ParamUtil.getInteger(actionRequest, "fromDateHour");
        int fromDateMinute = ParamUtil.getInteger(actionRequest, "fromDateMinute");

        // TODO: clean this up!
        // Create the untilDate with the date values of
        // the fromDate, because we want the user to
        // have to select only one date.
        int untilDateDay = ParamUtil.getInteger(actionRequest, "fromDateDay");
        int untilDateMonth = ParamUtil.getInteger(actionRequest, "fromDateMonth");
        int untilDateYear = ParamUtil.getInteger(actionRequest, "fromDateYear");
        int untilDateHour = ParamUtil.getInteger(actionRequest, "untilDateHour");
        int untilDateMinute = ParamUtil.getInteger(actionRequest, "untilDateMinute");

        Date fromDate = null;

        try {
            fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay, fromDateYear, fromDateHour, fromDateMinute,
                    TaskRecordUntilDateException.class);
        } catch (Exception e) {
            _log.error(e);
        }

        Date untilDate = null;

        try {
            untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay, untilDateYear, untilDateHour, untilDateMinute,
                    TaskRecordFromDateException.class);
        } catch (Exception e) {
            _log.error(e);
        }

        long fromTime = fromDate.getTime();
        long untilTime = untilDate.getTime();

        if (fromTime > untilTime) {
            untilDate = new Date(untilTime + 1000 * 60 * 60 * 24);
            untilTime = untilDate.getTime();
        }

        if (duration == 0) {
            duration = untilTime - fromTime;
        }


        TaskRecord taskRecord = null;
        
        if (taskRecordId <= 0) {

            // Add taskRecord

            taskRecord = _taskRecordService.addTaskRecord(workPackage, description, ticketURL, untilDate, fromDate,
                    status, duration, serviceContext);

        } else {

            // Update taskRecord

            taskRecord = _taskRecordService.updateTaskRecord(taskRecordId, workPackage, description, ticketURL,
                    untilDate, fromDate, status, duration, serviceContext);
        }

        actionRequest.setAttribute(TimetrackerWebKeys.TASK_RECORD, taskRecord);
    }

    private TaskRecordService _taskRecordService;
    
    private static Log _log = LogFactoryUtil.getLog(EditTaskRecordMVCActionCommand.class.getName()); 

}
