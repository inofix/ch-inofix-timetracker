package ch.inofix.timetracker.web.internal.search;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

/**
 * 
 * @author Christian Berndt
 * @created 2013-10-06 18:26
 * @modified 2016-11-26 14:15
 * @version 1.0.1
 * 
 */
public class TaskRecordSearchTerms extends TaskRecordDisplayTerms {

    public TaskRecordSearchTerms(PortletRequest portletRequest) {

        super(portletRequest);

        description = DAOParamUtil.getString(portletRequest, DESCRIPTION);
        endDate = DAOParamUtil.getString(portletRequest, END_DATE);
        groupId = DAOParamUtil.getLong(portletRequest, GROUP_ID);
        startDate = DAOParamUtil.getString(portletRequest, START_DATE);
        status = DAOParamUtil.getInteger(portletRequest, STATUS);
        userId = DAOParamUtil.getLong(portletRequest, USER_ID);
        workPackage = DAOParamUtil.getString(portletRequest, WORK_PACKAGE);

    }

}
