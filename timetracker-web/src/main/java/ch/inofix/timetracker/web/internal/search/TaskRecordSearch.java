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
 * @author Stefan Luebbers
 * @created 2013-10-06 18:26
 * @modified 2017-07-15 23.54
 * @version 1.0.6
 *
 */
public class TaskRecordSearch extends SearchContainer<TaskRecord> {

    public static final String EMPTY_RESULTS_MESSAGE = "no-task-records-were-found";

    static List<String> headerNames = new ArrayList<String>();
    static Map<String, String> orderableHeaders = new HashMap<String, String>();

    static {
        headerNames.add("create-date");
        headerNames.add("description");
        headerNames.add("duration");
        headerNames.add("from-date");
        headerNames.add("modified-date");
        headerNames.add("status"); // has no "name" in search_columns.jspf
        headerNames.add("task-record-id");
        headerNames.add("ticket-url");
        headerNames.add("work-package");
        headerNames.add("user-name");
        headerNames.add("until-date");

        orderableHeaders.put("create-date", "createDate_Number_sortable");
        orderableHeaders.put("description", "description_sortable");
        orderableHeaders.put("duration", "duration_Number_sortable");
        orderableHeaders.put("from-date", "fromDate_Number_sortable");
        orderableHeaders.put("modified-date", "modifiedDate_Number_sortable");
        orderableHeaders.put("status", "status_Number_sortable");
        orderableHeaders.put("task-record-id", "taskRecordId_Number_sortable");
        orderableHeaders.put("ticket-url", "ticketURL_sortable");
        orderableHeaders.put("until-date", "untilDate_Number_sortable");
        orderableHeaders.put("user-name", "userName_sortable");
        orderableHeaders.put("work-package", "workPackage_sortable");
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
                orderByCol = preferences.getValue(portletId, "task-records-order-by-col", "modified-date");
                orderByType = preferences.getValue(portletId, "task-records-order-by-type", "asc");
            }

            setOrderableHeaders(orderableHeaders);

            if (Validator.isNotNull(orderableHeaders.get(orderByCol))) {
                setOrderByCol(orderableHeaders.get(orderByCol));
            } else {
                _log.error(orderByCol + " is not an orderable header.");
                setOrderByCol(orderByCol);
            }

            setOrderByType(orderByType);

        } catch (Exception e) {
            _log.error(e);
        }
    }

    public static final Log _log = LogFactoryUtil.getLog(TaskRecordSearch.class.getName());
}
