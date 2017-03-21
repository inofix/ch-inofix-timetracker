package ch.inofix.timetracker.web.internal.portlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.exception.NoSuchResourceException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.thoughtworks.xstream.XStream;

import aQute.bnd.annotation.metatype.Configurable;
import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.exception.NoSuchTaskRecordException;
import ch.inofix.timetracker.exception.TaskRecordEndDateException;
import ch.inofix.timetracker.exception.TaskRecordStartDateException;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.model.impl.TaskRecordImpl;
import ch.inofix.timetracker.service.TaskRecordLocalService;
import ch.inofix.timetracker.service.TaskRecordService;
import ch.inofix.timetracker.web.configuration.TimetrackerConfiguration;
import ch.inofix.timetracker.web.internal.constants.TimetrackerWebKeys;
import ch.inofix.timetracker.web.internal.portlet.util.PortletUtil;

/**
 * View Controller of Inofix' timetracker.
 *
 * @author Christian Berndt
 * @created 2013-10-07 10:47
 * @modified 2017-03-21 14:57
 * @version 1.5.8
 */
@Component(immediate = true, property = { "com.liferay.portlet.css-class-wrapper=portlet-timetracker",
        "com.liferay.portlet.display-category=category.inofix", "com.liferay.portlet.header-portlet-css=/css/main.css",
        "com.liferay.portlet.instanceable=false", "javax.portlet.display-name=Timetracker",
        "javax.portlet.init-param.template-path=/", "javax.portlet.init-param.view-template=/view.jsp",
        "javax.portlet.resource-bundle=content.Language",
        "javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class TimetrackerPortlet extends MVCPortlet {

    /**
     * @param actionRequest
     * @param actionResponse
     * @since 1.0.8
     * @throws Exception
     */
    public void deleteAllTaskRecords(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        String tabs1 = ParamUtil.getString(actionRequest, "tabs1");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(TaskRecord.class.getName(), actionRequest);

        // TODO: use remote service
        List<TaskRecord> taskRecords = _taskRecordLocalService.getGroupTaskRecords(serviceContext.getScopeGroupId());

        for (TaskRecord taskRecord : taskRecords) {

            // TODO: Add try-catch and count failed deletions
            taskRecord = _taskRecordLocalService.deleteTaskRecord(taskRecord.getTaskRecordId());

        }

        SessionMessages.add(actionRequest, REQUEST_PROCESSED,
                PortletUtil.translate("successfully-deleted-x-task-records"));

        actionResponse.setRenderParameter("tabs1", tabs1);
    }

    /**
     *
     * @param actionRequest
     * @param actionResponse
     * @throws Exception
     */
    public void deleteTaskRecord(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long taskRecordId = ParamUtil.getLong(actionRequest, "taskRecordId");

        _taskRecordService.deleteTaskRecord(taskRecordId);
    }

    /**
     * @since 1.1.4
     * @param actionRequest
     * @param actionResponse
     */
    public void importXML(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        ServiceContext serviceContext = ServiceContextFactory.getInstance(TaskRecord.class.getName(), actionRequest);

        UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(actionRequest);

        File file = uploadPortletRequest.getFile("file");

        if (Validator.isNotNull(file)) {

            com.liferay.portal.kernel.xml.Document document = SAXReaderUtil.read(file);

            List<Node> nodes = document.selectNodes("/taskRecords/" + TaskRecordImpl.class.getName());

            int numRecords = 0;

            XStream xstream = new XStream();

            ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

            long groupId = themeDisplay.getScopeGroupId();
            long userId = themeDisplay.getUserId();
            User user = UserLocalServiceUtil.getUser(userId);
            String userName = user.getFullName();

            for (Node node : nodes) {

                String xml = node.asXML();

                TaskRecord importRecord = (TaskRecord) xstream.fromXML(xml);

                long taskRecordId = importRecord.getTaskRecordId();
                long companyId = PortalUtil.getCompanyId(actionRequest);

                if (companyId != importRecord.getCompanyId()) {

                    // Data is not from this portal instance
                    importRecord.setCompanyId(companyId);
                }

                if (groupId != importRecord.getGroupId()) {

                    // Data is not from this group
                    importRecord.setGroupId(groupId);
                }

                User systemUser = null;
                try {
                    systemUser = UserLocalServiceUtil.getUser(importRecord.getUserId());
                } catch (NoSuchUserException nsue) {
                    _log.warn(nsue.getMessage());
                }

                if (systemUser == null) {

                    // The record's user does not exist in this system.
                    // Use the current user's id and userName instead.
                    importRecord.setUserId(userId);
                    importRecord.setUserName(userName);

                } else {

                    // Update the record with the system user's userName
                    importRecord.setUserName(systemUser.getFullName());
                }

                TaskRecord existingRecord = null;

                try {
                    existingRecord = _taskRecordLocalService.getTaskRecord(taskRecordId);
                } catch (NoSuchTaskRecordException ignore) {
                }

                if (existingRecord == null) {

                    if (importRecord.getTaskRecordId() == 0) {

                        // Insert the imported record as new
                        _taskRecordLocalService.addTaskRecord(importRecord.getUserId(), importRecord.getWorkPackage(),
                                importRecord.getDescription(), importRecord.getTicketURL(), importRecord.getEndDate(),
                                importRecord.getStartDate(), importRecord.getStatus(), importRecord.getDuration(),
                                serviceContext);
                    } else {

                        // Record already has an id but does not exist in this
                        // System.
                        _taskRecordLocalService.addTaskRecord(importRecord);
                    }

                }

                numRecords++;
            }

            SessionMessages.add(actionRequest, REQUEST_PROCESSED,
                    PortletUtil.translate("successfully-imported-x-task-records", numRecords));
        } else {
            SessionErrors.add(actionRequest, PortletUtil.translate("file-not-found"));
        }

    }

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        try {
            getTaskRecord(renderRequest);
        } catch (Exception e) {
            if (e instanceof NoSuchResourceException || e instanceof PrincipalException) {
                SessionErrors.add(renderRequest, e.getClass());
            } else {
                throw new PortletException(e);
            }
        }

        super.render(renderRequest, renderResponse);
    }

    /**
     *
     * @param actionRequest
     * @param actionResponse
     * @throws Exception
     */
    public void updateTaskRecord(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        _log.info("updateTaskRecord()");

        long taskRecordId = ParamUtil.getLong(actionRequest, "taskRecordId");

        _log.info("taskRecordId = " + taskRecordId);

        ServiceContext serviceContext = ServiceContextFactory.getInstance(TaskRecord.class.getName(), actionRequest);

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        long groupId = themeDisplay.getScopeGroupId();
        long userId = themeDisplay.getUserId();

        String workPackage = ParamUtil.getString(actionRequest, "workPackage");
        String description = ParamUtil.getString(actionRequest, "description");
        String ticketURL = ParamUtil.getString(actionRequest, "ticketURL");
        int durationInMinutes = ParamUtil.getInteger(actionRequest, "duration");
        long duration = durationInMinutes * 60 * 1000;
        int status = ParamUtil.getInteger(actionRequest, "status");

        int startDateDay = ParamUtil.getInteger(actionRequest, "startDateDay");
        int startDateMonth = ParamUtil.getInteger(actionRequest, "startDateMonth");
        int startDateYear = ParamUtil.getInteger(actionRequest, "startDateYear");
        int startDateHour = ParamUtil.getInteger(actionRequest, "startDateHour");
        int startDateMinute = ParamUtil.getInteger(actionRequest, "startDateMinute");

        // TODO: clean this up!
        // Create the endDate with the date values of
        // the startDate, because we want the user to
        // have to select only one date.
        int endDateDay = ParamUtil.getInteger(actionRequest, "startDateDay");
        int endDateMonth = ParamUtil.getInteger(actionRequest, "startDateMonth");
        int endDateYear = ParamUtil.getInteger(actionRequest, "startDateYear");
        int endDateHour = ParamUtil.getInteger(actionRequest, "endDateHour");
        int endDateMinute = ParamUtil.getInteger(actionRequest, "endDateMinute");

        Date endDate = null;

        try {
            PortalUtil.getDate(endDateMonth, endDateDay, endDateYear, endDateHour, endDateMinute,
                    TaskRecordEndDateException.class);
        } catch (Exception e) {
            _log.error(e);
        }

        Date startDate = null;

        try {
            PortalUtil.getDate(startDateMonth, startDateDay, startDateYear, startDateHour, startDateMinute,
                    TaskRecordStartDateException.class);
        } catch (Exception e) {
            _log.error(e);
        }

        TaskRecord taskRecord = null;

        if (taskRecordId <= 0) {

            // Add taskRecord

            _log.info("add taskRecord");

            // TODO: Use remote service
            taskRecord = _taskRecordLocalService.addTaskRecord(userId, workPackage, description, ticketURL, endDate,
                    startDate, status, duration, serviceContext);

        } else {

            // Update taskRecord

            _log.info("update taskRecord");

            // TODO: Use remote service
            taskRecord = _taskRecordLocalService.updateTaskRecord(userId, taskRecordId, workPackage, description,
                    ticketURL, endDate, startDate, status, duration, serviceContext);
        }

        String redirect = getEditTaskRecordURL(actionRequest, actionResponse, taskRecord);

        actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
    }

    @Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {
        _timetrackerConfiguration = Configurable.createConfigurable(TimetrackerConfiguration.class, properties);
    }

    @Override
    protected void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        _log.info("doDispatch()");

        if (SessionErrors.contains(renderRequest, PrincipalException.getNestedClasses())
                || SessionErrors.contains(renderRequest, NoSuchTaskRecordException.class)) {
            include("/error.jsp", renderRequest, renderResponse);
        } else {
            super.doDispatch(renderRequest, renderResponse);
        }
    }

    @Override
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        renderRequest.setAttribute(TimetrackerConfiguration.class.getName(), _timetrackerConfiguration);

        super.doView(renderRequest, renderResponse);
    }

    protected String getEditTaskRecordURL(ActionRequest actionRequest, ActionResponse actionResponse,
            TaskRecord taskRecord) throws Exception {

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        String editTaskRecordURL = getRedirect(actionRequest, actionResponse);

        if (Validator.isNull(editTaskRecordURL)) {
            editTaskRecordURL = PortalUtil.getLayoutFullURL(themeDisplay);
        }

        String namespace = actionResponse.getNamespace();
        String windowState = actionResponse.getWindowState().toString();

        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, "p_p_id", PortletKeys.TIMETRACKER);
        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, "p_p_state", windowState);
        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "mvcPath",
                templatePath + "edit_task_record.jsp");
        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "redirect",
                getRedirect(actionRequest, actionResponse));
        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "backURL",
                ParamUtil.getString(actionRequest, "backURL"));
        editTaskRecordURL = HttpUtil.setParameter(editTaskRecordURL, namespace + "taskRecordId",
                taskRecord.getTaskRecordId());

        _log.info(editTaskRecordURL);

        return editTaskRecordURL;
    }

    protected void getTaskRecord(PortletRequest portletRequest) throws Exception {

        long taskRecordId = ParamUtil.getLong(portletRequest, "taskRecordId");

        if (taskRecordId <= 0) {
            return;
        }

        TaskRecord taskRecord = _taskRecordService.getTaskRecord(taskRecordId);

        portletRequest.setAttribute(TimetrackerWebKeys.TASK_RECORD, taskRecord);
    }

    // TODO: Remove local service from portlet
    @Reference
    protected void setTaskRecordLocalService(TaskRecordLocalService taskRecordLocalService) {
        this._taskRecordLocalService = taskRecordLocalService;
    }

    @Reference
    protected void setTaskRecordService(TaskRecordService taskRecordService) {
        this._taskRecordService = taskRecordService;
    }

    // TODO: Remove local service from portlet
    private TaskRecordLocalService _taskRecordLocalService;
    private TaskRecordService _taskRecordService;

    private volatile TimetrackerConfiguration _timetrackerConfiguration;

    private static final String REQUEST_PROCESSED = "request_processed";

    private static final Log _log = LogFactoryUtil.getLog(TimetrackerPortlet.class.getName());

}
