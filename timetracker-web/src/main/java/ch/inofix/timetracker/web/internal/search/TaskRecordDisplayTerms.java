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
 * @modified 2016-11-17 00:32
 * @version 1.0.1
 * 
 */
public class TaskRecordDisplayTerms extends DisplayTerms {

	public static final String CREATE_DATE = "createDate";
    public static final String DESCRIPTION = "description";
    public static final String DURATION = "duration";
    public static final String END_DATE = "endDate";
    public static final String GROUP_ID = "groupId";
    public static final String MODIFIED_DATE = "modifiedDate";
    public static final String START_DATE = "startDate";
    public static final String STATUS = "status";
    public static final String TASKRECORDID = "taskRecordId";
    public static final String TICKET_URL = "ticketURL";
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String WORK_PACKAGE = "workPackage";

    public TaskRecordDisplayTerms(PortletRequest portletRequest) {

        super(portletRequest);

        description = ParamUtil.getString(portletRequest, DESCRIPTION);
        endDate = ParamUtil.getString(portletRequest, END_DATE);
        groupId = ParamUtil.getLong(portletRequest, GROUP_ID);
        startDate = ParamUtil.getString(portletRequest, START_DATE);
        String statusString = ParamUtil.getString(portletRequest, STATUS);

        if (Validator.isNotNull(statusString)) {
            status = GetterUtil.getInteger(statusString);
        }
        userId = ParamUtil.getLong(portletRequest, USER_ID);
        workPackage = ParamUtil.getString(portletRequest, WORK_PACKAGE);
        createDate = ParamUtil.getString(portletRequest, CREATE_DATE);
        taskRecordId = ParamUtil.getLong(portletRequest, TASKRECORDID);
        modifiedDate = ParamUtil.getString(portletRequest, MODIFIED_DATE);
        duration = ParamUtil.getLong(portletRequest, DURATION);
        userName = ParamUtil.getString(portletRequest, USER_NAME);
        ticketURL = ParamUtil.getString(portletRequest, TICKET_URL);
    }

    public String getWorkPackage() {
        return workPackage;
    }

    public void setWorkPackage(String workPackage) {
        this.workPackage = workPackage;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public long getTaskRecordId() {
		return taskRecordId;
	}

	public void setTaskRecordId(long taskRecordId) {
		this.taskRecordId = taskRecordId;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTicketURL() {
		return ticketURL;
	}

	public void setTicketURL(String ticketURL) {
		this.ticketURL = ticketURL;
	}

	protected String createDate;
    protected String description;
    protected String endDate;
    protected long groupId;
    protected String startDate;
    protected int status;
    protected long userId;
    protected String workPackage;
    protected long taskRecordId;
    protected String modifiedDate;
    protected long duration;
    protected String userName;
    protected String ticketURL;

}
