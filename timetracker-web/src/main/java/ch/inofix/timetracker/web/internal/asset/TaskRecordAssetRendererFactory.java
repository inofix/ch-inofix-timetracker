package ch.inofix.timetracker.web.internal.asset;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.constants.TaskRecordActionKeys;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalService;
import ch.inofix.timetracker.service.permission.TaskRecordPermission;
import ch.inofix.timetracker.service.permission.TimetrackerPortletPermission;

/**
 *
 * @author Christian Berndt
 * @created 2017-03-21 13:52
 * @modified 2017-07-09 18:02
 * @version 1.0.2
 *
 */
@Component(immediate = true, property = {
        "javax.portlet.name=" + PortletKeys.TIMETRACKER }, service = AssetRendererFactory.class)
public class TaskRecordAssetRendererFactory extends BaseAssetRendererFactory<TaskRecord> {

    public static final String TYPE = "task_record";

    public TaskRecordAssetRendererFactory() {

        setCategorizable(true);
        setClassName(TaskRecord.class.getName());
        setLinkable(true);
        setPortletId(PortletKeys.TIMETRACKER);
        setSearchable(true);
        setSelectable(true);

    }

    @Override
    public AssetRenderer<TaskRecord> getAssetRenderer(long classPK, int type) throws PortalException {

        TaskRecord taskRecord = _taskRecordLocalService.getTaskRecord(classPK);

        TaskRecordAssetRenderer taskRecordAssetRenderer = new TaskRecordAssetRenderer(taskRecord);

        taskRecordAssetRenderer.setAssetRendererType(type);
        taskRecordAssetRenderer.setServletContext(_servletContext);

        return taskRecordAssetRenderer;

    }

    @Override
    public String getClassName() {
        return TaskRecord.class.getName();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public PortletURL getURLAdd(LiferayPortletRequest liferayPortletRequest,
            LiferayPortletResponse liferayPortletResponse) throws PortalException {

        ThemeDisplay themeDisplay = (ThemeDisplay) liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY);

        User user = themeDisplay.getUser();

        Group group = user.getGroup();

        if (group != null) {

            long portletPlid = PortalUtil.getPlidFromPortletId(group.getGroupId(), false, PortletKeys.TIMETRACKER);

            PortletURL portletURL = PortletURLFactoryUtil.create(liferayPortletRequest, PortletKeys.TIMETRACKER,
                    portletPlid, PortletRequest.RENDER_PHASE);

            portletURL.setParameter("mvcPath", "/edit_task_record.jsp");

            String redirect = (String) liferayPortletRequest.getAttribute("redirect");

            if (Validator.isNotNull(redirect)) {
                portletURL.setParameter("redirect", redirect);
            }

            return portletURL;

        } else {

            return null;

        }
    }

    @Override
    public boolean hasAddPermission(PermissionChecker permissionChecker, long groupId, long classTypeId)
            throws Exception {

        return TimetrackerPortletPermission.contains(permissionChecker, groupId, TaskRecordActionKeys.ADD_TASK_RECORD);
    }

    @Override
    public boolean hasPermission(PermissionChecker permissionChecker, long classPK, String actionId) throws Exception {

        TaskRecord taskRecord = _taskRecordLocalService.getTaskRecord(classPK);

        return TaskRecordPermission.contains(permissionChecker, taskRecord.getTaskRecordId(), actionId);
    }

    @Reference(target = "(osgi.web.symbolicname=ch.inofix.timetracker.web)", unbind = "-")
    public void setServletContext(ServletContext servletContext) {
        _servletContext = servletContext;
    }

    @Reference(unbind = "-")
    protected void setTaskRecordLocalService(TaskRecordLocalService taskRecordLocalService) {
        _taskRecordLocalService = taskRecordLocalService;
    }

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordAssetRendererFactory.class);

    private TaskRecordLocalService _taskRecordLocalService;
    private ServletContext _servletContext;

}
