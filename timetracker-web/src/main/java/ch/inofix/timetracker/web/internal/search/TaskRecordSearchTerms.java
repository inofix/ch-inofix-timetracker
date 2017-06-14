package ch.inofix.timetracker.web.internal.search;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

/**
 *
 * @author Christian Berndt
 * @created 2013-10-06 18:26
 * @modified 2017-06-14 21:58
 * @version 1.0.3
 *
 */
public class TaskRecordSearchTerms extends TaskRecordDisplayTerms {

    public TaskRecordSearchTerms(PortletRequest portletRequest) {

        super(portletRequest);

        description = DAOParamUtil.getString(portletRequest, DESCRIPTION);
        fromDate = DAOParamUtil.getString(portletRequest, FROM_DATE);
        groupId = DAOParamUtil.getLong(portletRequest, GROUP_ID);
        ownerUserId = DAOParamUtil.getLong(portletRequest, OWNER_USER_ID);
        status = DAOParamUtil.getInteger(portletRequest, STATUS);
        untilDate = DAOParamUtil.getString(portletRequest, UNTIL_DATE);
        workPackage = DAOParamUtil.getString(portletRequest, WORK_PACKAGE);

    }
}
