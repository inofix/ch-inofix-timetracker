package ch.inofix.timetracker.web.internal.search;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 *
 * @author Christian Berndt
 * @created 2013-10-06 17:34
 * @modified 2017-06-09 17:53
 * @version 1.0.2
 *
 */
public class TaskRecordDisplayTerms extends DisplayTerms {

    public static final String CREATE_DATE = "createDate";
    public static final String DESCRIPTION = "description";
    public static final String DURATION = "duration";
    public static final String FROM_DATE = "fromDate";
    public static final String GROUP_ID = "groupId";
    public static final String MODIFIED_DATE = "modifiedDate";
    public static final String STATUS = "status";
    public static final String TASKRECORDID = "taskRecordId";
    public static final String TICKET_URL = "ticketURL";
    public static final String UNTIL_DATE = "untilDate";
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String WORK_PACKAGE = "workPackage";

    public TaskRecordDisplayTerms(PortletRequest portletRequest) {

        super(portletRequest);

        createDate = ParamUtil.getString(portletRequest, CREATE_DATE);
        description = ParamUtil.getString(portletRequest, DESCRIPTION);
        duration = ParamUtil.getLong(portletRequest, DURATION);
        fromDate = ParamUtil.getString(portletRequest, FROM_DATE);
        groupId = ParamUtil.getLong(portletRequest, GROUP_ID);
        modifiedDate = ParamUtil.getString(portletRequest, MODIFIED_DATE);
        String statusString = ParamUtil.getString(portletRequest, STATUS);

        if (Validator.isNotNull(statusString)) {
            status = GetterUtil.getInteger(statusString);
        }
        ticketURL = ParamUtil.getString(portletRequest, TICKET_URL);
        taskRecordId = ParamUtil.getLong(portletRequest, TASKRECORDID);
        untilDate = ParamUtil.getString(portletRequest, UNTIL_DATE);
        userId = ParamUtil.getLong(portletRequest, USER_ID);
        userName = ParamUtil.getString(portletRequest, USER_NAME);
        workPackage = ParamUtil.getString(portletRequest, WORK_PACKAGE);
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTaskRecordId() {
        return taskRecordId;
    }

    public void setTaskRecordId(long taskRecordId) {
        this.taskRecordId = taskRecordId;
    }

    public String getTicketURL() {
        return ticketURL;
    }

    public void setTicketURL(String ticketURL) {
        this.ticketURL = ticketURL;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getWorkPackage() {
        return workPackage;
    }

    public void setWorkPackage(String workPackage) {
        this.workPackage = workPackage;
    }

    public String getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(String untilDate) {
        this.untilDate = untilDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    protected String createDate;
    protected String description;
    protected long duration;
    protected String fromDate;
    protected long groupId;
    protected String modifiedDate;
    protected int status;
    protected long taskRecordId;
    protected String ticketURL;
    protected long userId;
    protected String workPackage;
    protected String untilDate;
    protected String userName;

}
