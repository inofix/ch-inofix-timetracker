package ch.inofix.timetracker.web.internal.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import ch.inofix.timetracker.model.TaskRecord;

/**
 *
 * @author Christian Berndt
 * @Stefan Luebbers
 * @created 2013-10-06 18:26
 * @modified 2017-06-09 17:59
 * @version 1.0.2
 *
 */
public class TaskRecordSearch extends SearchContainer<TaskRecord> {

    public static final String EMPTY_RESULTS_MESSAGE = "no-task-records-were-found";

    static List<String> headerNames = new ArrayList<String>();
    static Map<String, String> orderableHeaders = new HashMap<String, String>();

    static {
        headerNames.add("task-record-id");
        headerNames.add("status"); // has no "name" in search_columns.jspf
        headerNames.add("work-package");
        headerNames.add("user-name");
        headerNames.add("ticket-url");
        headerNames.add("description");
        headerNames.add("create-date");
        headerNames.add("modified-date");
        headerNames.add("start-date");
        headerNames.add("end-date");
        headerNames.add("duration");

        orderableHeaders.put("taskRecordId", "taskRecordId");
        orderableHeaders.put("status", "status");
        orderableHeaders.put("workPackage", "workPackage");
        orderableHeaders.put("userName", "userName");
        orderableHeaders.put("ticketURL", "ticketURL");
        orderableHeaders.put("description", "description");
        orderableHeaders.put("createDate", "createDate");
        orderableHeaders.put("modifiedDate", "modifiedDate");
        orderableHeaders.put("startDate", "startDate");
        orderableHeaders.put("endDate", "endDate");
        orderableHeaders.put("duration", "duration");
    }

    public TaskRecordSearch(PortletRequest portletRequest, PortletURL iteratorURL) {

        this(portletRequest, DEFAULT_CUR_PARAM, iteratorURL);
    }

    public TaskRecordSearch(PortletRequest portletRequest, String curParam, PortletURL iteratorURL) {

        super(portletRequest, new TaskRecordDisplayTerms(portletRequest), new TaskRecordSearchTerms(portletRequest),
                curParam, DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

        PortletConfig portletConfig = (PortletConfig) portletRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);

        TaskRecordDisplayTerms displayTerms = (TaskRecordDisplayTerms) getDisplayTerms();
        TaskRecordSearchTerms searchTerms = (TaskRecordSearchTerms) getSearchTerms();

        String portletId = PortletProviderUtil.getPortletId(User.class.getName(), PortletProvider.Action.VIEW);
        String portletName = portletConfig.getPortletName();

        if (!portletId.equals(portletName)) {
            displayTerms.setStatus(WorkflowConstants.STATUS_APPROVED);
            searchTerms.setStatus(WorkflowConstants.STATUS_APPROVED);
        }

        iteratorURL.setParameter(TaskRecordDisplayTerms.TASKRECORDID, String.valueOf(displayTerms.getTaskRecordId()));
        iteratorURL.setParameter(TaskRecordDisplayTerms.STATUS, String.valueOf(displayTerms.getStatus()));
        iteratorURL.setParameter(TaskRecordDisplayTerms.WORK_PACKAGE, displayTerms.getWorkPackage());
        iteratorURL.setParameter(TaskRecordDisplayTerms.DESCRIPTION, displayTerms.getDescription());
        iteratorURL.setParameter(TaskRecordDisplayTerms.USER_NAME, displayTerms.getUserName());
        iteratorURL.setParameter(TaskRecordDisplayTerms.TICKET_URL, displayTerms.getTicketURL());
        iteratorURL.setParameter(TaskRecordDisplayTerms.CREATE_DATE, displayTerms.getCreateDate());
        iteratorURL.setParameter(TaskRecordDisplayTerms.MODIFIED_DATE, displayTerms.getModifiedDate());
        iteratorURL.setParameter(TaskRecordDisplayTerms.FROM_DATE, displayTerms.getFromDate());
        iteratorURL.setParameter(TaskRecordDisplayTerms.UNTIL_DATE, displayTerms.getUntilDate());
        iteratorURL.setParameter(TaskRecordDisplayTerms.DURATION, String.valueOf(displayTerms.getDuration()));

        try {
            PortalPreferences preferences = PortletPreferencesFactoryUtil.getPortalPreferences(portletRequest);

            String orderByCol = ParamUtil.getString(portletRequest, "orderByCol");
            String orderByType = ParamUtil.getString(portletRequest, "orderByType");

            if (Validator.isNotNull(orderByCol) && Validator.isNotNull(orderByType)) {

                preferences.setValue(portletId, "task-records-order-by-col", orderByCol);
                preferences.setValue(portletId, "task-records-order-by-type", orderByType);
            } else {
                orderByCol = preferences.getValue(portletId, "task-records-order-by-col", "last-name");
                orderByType = preferences.getValue(portletId, "task-records-order-by-type", "asc");
            }

            setOrderableHeaders(orderableHeaders);
            setOrderByCol(orderByCol);
            setOrderByType(orderByType);

        } catch (Exception e) {
            _log.error(e);
        }
    }

    public static final Log _log = LogFactoryUtil.getLog(TaskRecordSearch.class.getName());
}
