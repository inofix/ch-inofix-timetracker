package ch.inofix.timetracker.web.internal.portlet.action;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import ch.inofix.timetracker.constants.PortletKeys;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-11 13:29
 * @modified 2017-11-11 13:29
 * @version 1.0.0
 *
 */
@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + PortletKeys.TIMETRACKER,
        "mvc.command.name=editTaskRecord"
    },
    service = MVCRenderCommand.class
)
public class EditTaskRecordMVCRenderCommand extends GetTaskRecordMVCRenderCommand {

    @Override
    protected String getPath() {

        return "/edit_task_record.jsp";
    }
}
