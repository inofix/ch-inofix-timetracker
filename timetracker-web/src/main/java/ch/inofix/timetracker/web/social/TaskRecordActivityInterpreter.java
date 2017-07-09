package ch.inofix.timetracker.web.social;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.social.kernel.model.BaseSocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalService;
import ch.inofix.timetracker.service.permission.TaskRecordPermission;
import ch.inofix.timetracker.social.TaskRecordActivityKeys;

/**
 * @author Christian Berndt
 * @created 2017-07-09 17:17
 * @modified 2017-07-09 17:17
 * @version 1.0.0
 */
@Component(property = { "javax.portlet.name=" + PortletKeys.TIMETRACKER }, service = SocialActivityInterpreter.class)
public class TaskRecordActivityInterpreter extends BaseSocialActivityInterpreter {

    @Override
    public String[] getClassNames() {
        return _CLASS_NAMES;
    }

    @Override
    protected String getPath(SocialActivity activity, ServiceContext serviceContext) throws Exception {

        _log.info("getPath");

        AssetRendererFactory<?> assetRendererFactory = AssetRendererFactoryRegistryUtil
                .getAssetRendererFactoryByClassName(TaskRecord.class.getName());

        AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(activity.getClassPK());

        _log.info(assetRenderer);

        String path = assetRenderer.getURLViewInContext(serviceContext.getLiferayPortletRequest(),
                serviceContext.getLiferayPortletResponse(), null);

        path = HttpUtil.addParameter(path, "redirect", serviceContext.getCurrentURL());

        return path;
    }

    @Override
    protected ResourceBundleLoader getResourceBundleLoader() {
        return _resourceBundleLoader;
    }

    @Override
    protected String getTitlePattern(String groupName, SocialActivity activity) {

        _log.info("getTitlePattern");

        int activityType = activity.getType();

        if (activityType == TaskRecordActivityKeys.ADD_TASK_RECORD) {
            if (Validator.isNull(groupName)) {
                return "activity-task-record-add-task-record";
            } else {
                return "activity-task-record-add-task-record-in";
            }
        } else if (activityType == SocialActivityConstants.TYPE_MOVE_TO_TRASH) {
            if (Validator.isNull(groupName)) {
                return "activity-task-record-move-to-trash";
            } else {
                return "activity-task-record-move-to-trash-in";
            }
        } else if (activityType == SocialActivityConstants.TYPE_RESTORE_FROM_TRASH) {

            if (Validator.isNull(groupName)) {
                return "activity-task-record-restore-from-trash";
            } else {
                return "activity-task-record-restore-from-trash-in";
            }
        } else if (activityType == TaskRecordActivityKeys.UPDATE_TASK_RECORD) {
            if (Validator.isNull(groupName)) {
                return "activity-task-record-update-task-record";
            } else {
                return "activity-task-record-update-task-record-in";
            }
        }

        return StringPool.BLANK;
    }

    @Override
    protected boolean hasPermissions(PermissionChecker permissionChecker, SocialActivity activity, String actionId,
            ServiceContext serviceContext) throws Exception {

        TaskRecord taskRecord = _taskRecordLocalService.getTaskRecord(activity.getClassPK());

        return TaskRecordPermission.contains(permissionChecker, taskRecord.getTaskRecordId(), actionId);
    }

    @Reference(unbind = "-")
    protected void setTaskRecordLocalService(TaskRecordLocalService taskRecordLocalService) {
        _taskRecordLocalService = taskRecordLocalService;
    }

    private static final String[] _CLASS_NAMES = { TaskRecord.class.getName() };

    private TaskRecordLocalService _taskRecordLocalService;
    private ResourceBundleLoader _resourceBundleLoader;

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordActivityInterpreter.class.getName());

}
