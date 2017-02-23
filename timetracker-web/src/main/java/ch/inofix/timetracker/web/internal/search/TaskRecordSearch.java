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
 * @created 2013-10-06 18:26
 * @modified 2016-11-26 14:04
 * @version 1.0.1
 * 
 */
public class TaskRecordSearch extends SearchContainer<TaskRecord> {

    public static final String EMPTY_RESULTS_MESSAGE = "no-task-records-were-found";

    static List<String> headerNames = new ArrayList<String>();
    static Map<String, String> orderableHeaders = new HashMap<String, String>();

    static {
        headerNames.add("taskRecordId");
        headerNames.add("workPackage");
        headerNames.add("description");
        headerNames.add("startDate");
        headerNames.add("endDate");
        headerNames.add("duration");

        orderableHeaders.put("taskRecordId", "taskRecordId");
        orderableHeaders.put("workPackage", "workPackage");
        orderableHeaders.put("description", "description");
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

        // TODO: add other iterator relevant parameters
        iteratorURL.setParameter(TaskRecordDisplayTerms.STATUS, String.valueOf(displayTerms.getStatus()));

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
