package ch.inofix.timetracker.web.internal.portlet.action;

import java.util.Date;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.exception.NoSuchTaskRecordException;
import ch.inofix.timetracker.exception.TaskRecordFromDateException;
import ch.inofix.timetracker.exception.TaskRecordUntilDateException;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordService;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-10 19:09
 * @modified 2017-11-11 13:28
 * @version 1.0.1
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

        _log.info("doProcessAction");
        _log.info("cmd = " + cmd);

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        TaskRecord taskRecord = null;
        try {

            if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
                taskRecord = updateTaskRecord(actionRequest);
            } else if (cmd.equals(Constants.DELETE)) {
                deleteTaskRecords(actionRequest);
            } else if (cmd.equals("deleteGroupTaskRecords")) {
                deleteGroupTaskRecords(actionRequest);
            }

            if (Validator.isNotNull(cmd)) {
                String redirect = ParamUtil.getString(actionRequest, "redirect");
                if (taskRecord != null) {

                    redirect = getSaveAndContinueRedirect(actionRequest, taskRecord, themeDisplay.getLayout(),
                            redirect);

                    sendRedirect(actionRequest, actionResponse, redirect);
                }
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
    
    protected String getSaveAndContinueRedirect(
            ActionRequest actionRequest, TaskRecord taskRecord, Layout layout, String redirect)
        throws Exception {

        PortletConfig portletConfig = (PortletConfig)actionRequest.getAttribute(
            JavaConstants.JAVAX_PORTLET_CONFIG);
        
        LiferayPortletURL portletURL = PortletURLFactoryUtil.create(actionRequest, portletConfig.getPortletName(), layout, PortletRequest.RENDER_PHASE);

        portletURL.setParameter("mvcRenderCommandName", "editTaskRecord");

        portletURL.setParameter(Constants.CMD, Constants.UPDATE, false);
        portletURL.setParameter("redirect", redirect, false);
        portletURL.setParameter(
            "groupId", String.valueOf(taskRecord.getGroupId()), false);
        portletURL.setParameter(
            "taskRecordId", String.valueOf(taskRecord.getTaskRecordId()), false);
        portletURL.setWindowState(actionRequest.getWindowState());

        return portletURL.toString();
    }

    @Reference(unbind = "-")
    protected void setTaskRecordService(TaskRecordService taskRecordService) {
        this._taskRecordService = taskRecordService;
    }

    protected TaskRecord updateTaskRecord(ActionRequest actionRequest) throws Exception {
        
        _log.info("updateTaskRecord");

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
                
        return taskRecord;
    }
    
    @Reference
    private Http _http;

    @Reference
    private Portal _portal;
    
    private TaskRecordService _taskRecordService;

    private static Log _log = LogFactoryUtil.getLog(EditTaskRecordMVCActionCommand.class.getName()); 

}
