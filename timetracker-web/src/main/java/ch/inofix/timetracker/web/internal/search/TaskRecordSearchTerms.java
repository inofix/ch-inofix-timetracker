package ch.inofix.timetracker.web.internal.search;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

/**
 *
 * @author Christian Berndt
 * @created 2013-10-06 18:26
 * @modified 2017-06-09 18:01
 * @version 1.0.2
 *
 */
public class TaskRecordSearchTerms extends TaskRecordDisplayTerms {

    public TaskRecordSearchTerms(PortletRequest portletRequest) {

        super(portletRequest);

        description = DAOParamUtil.getString(portletRequest, DESCRIPTION);
        fromDate = DAOParamUtil.getString(portletRequest, FROM_DATE);
        groupId = DAOParamUtil.getLong(portletRequest, GROUP_ID);
        status = DAOParamUtil.getInteger(portletRequest, STATUS);
        untilDate = DAOParamUtil.getString(portletRequest, UNTIL_DATE);
        userId = DAOParamUtil.getLong(portletRequest, USER_ID);
        workPackage = DAOParamUtil.getString(portletRequest, WORK_PACKAGE);

    }
}
