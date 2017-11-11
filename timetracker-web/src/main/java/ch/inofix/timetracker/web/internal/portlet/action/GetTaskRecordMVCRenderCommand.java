package ch.inofix.timetracker.web.internal.portlet.action;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;

import ch.inofix.timetracker.exception.NoSuchTaskRecordException;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.web.internal.constants.TimetrackerWebKeys;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-10 23:49
 * @modified 2017-11-10 23:49
 * @version 1.0.0
 *
 */
public abstract class GetTaskRecordMVCRenderCommand implements MVCRenderCommand {

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

        _log.info("render()");

        try {
            TaskRecord taskRecord = ActionUtil.getTaskRecord(renderRequest);

            renderRequest.setAttribute(TimetrackerWebKeys.TASK_RECORD, taskRecord);
        } catch (Exception e) {
            if (e instanceof NoSuchTaskRecordException || e instanceof PrincipalException) {

                SessionErrors.add(renderRequest, e.getClass());

                return "/error.jsp";

            } else {
                throw new PortletException(e);
            }
        }

        return getPath();
    }

    protected abstract String getPath();

    private static Log _log = LogFactoryUtil.getLog(GetTaskRecordMVCRenderCommand.class.getName());

}
