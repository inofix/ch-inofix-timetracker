package ch.inofix.timetracker.web.internal.portlet.action;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.timetracker.constants.TimetrackerActionKeys;
import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.service.permission.TimetrackerPortletPermission;

/**
 * @author Christian Berndt
 * @created 2017-11-17 22:56
 * @modified 2017-11-19 14:05
 * @version 1.0.1
 */
@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + PortletKeys.TIMETRACKER,
        "mvc.command.name=exportTaskRecords"
    },
    service = MVCRenderCommand.class
)
public class ExportTaskRecordsMVCRenderCommand implements MVCRenderCommand {

    protected String getPath() {

        return "/export/new_export/export_task_records.jsp";
    }

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

        ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

        try {

            TimetrackerPortletPermission.check(themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroupId(),
                    TimetrackerActionKeys.EXPORT_TASK_RECORDS);

        } catch (Exception e) {
            if (e instanceof PrincipalException) {

                SessionErrors.add(renderRequest, e.getClass());

                return "/error.jsp";

            } else {
                throw new PortletException(e);
            }
        }

        return getPath();

    }
}
