package ch.inofix.timetracker.web.internal.portlet.action;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.timetracker.constants.TimetrackerActionKeys;
import ch.inofix.timetracker.service.permission.TimetrackerPortletPermission;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import ch.inofix.timetracker.constants.PortletKeys;

/**
 * @author Christian Berndt
 * @created 2017-11-13 23:51
 * @modified 2017-11-13 23:51
 * @version 1.0.0
 */
@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + PortletKeys.TIMETRACKER,
        "mvc.command.name=importTaskRecords"
    },
    service = MVCRenderCommand.class
)
public class ImportTaskRecordsMVCRenderCommand implements MVCRenderCommand {

    protected String getPath() {

        return "/import/new_import/import_task_records.jsp";
    }

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

        ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

        try {

            TimetrackerPortletPermission.check(themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroupId(),
                    TimetrackerActionKeys.IMPORT_TASK_RECORDS);

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
