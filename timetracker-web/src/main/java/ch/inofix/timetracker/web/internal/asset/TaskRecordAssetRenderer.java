package ch.inofix.timetracker.web.internal.asset;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.PortalUtil;

import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.permission.TaskRecordPermission;
import ch.inofix.timetracker.web.internal.constants.TimetrackerWebKeys;

/**
 *
 * @author Christian Berndt
 * @created 2017-03-21 13:51
 * @modified 2017-03-21 13:51
 * @version 1.0.0
 *
 */
public class TaskRecordAssetRenderer extends BaseJSPAssetRenderer<TaskRecord> {

    public TaskRecordAssetRenderer(TaskRecord taskRecord) {
        _taskRecord = taskRecord;
    }

    @Override
    public TaskRecord getAssetObject() {
        return _taskRecord;
    }

    @Override
    public String getClassName() {
        return TaskRecord.class.getName();
    }

    @Override
    public long getClassPK() {
        return _taskRecord.getTaskRecordId();
    }

    @Override
    public long getGroupId() {
        return _taskRecord.getGroupId();
    }

    @Override
    public String getJspPath(HttpServletRequest request, String template) {
        if (template.equals(TEMPLATE_ABSTRACT) || template.equals(TEMPLATE_FULL_CONTENT)) {
            // TODO: add jsp-tempates
            return "/asset/" + template + ".jsp";
        } else {
            return null;
        }
    }

    public String getPortletId() {
        AssetRendererFactory<TaskRecord> assetRendererFactory = getAssetRendererFactory();

        return assetRendererFactory.getPortletId();
    }

    @Override
    public int getStatus() {
        return _taskRecord.getStatus();
    }

    @Override
    public String getSummary(PortletRequest portletRequest, PortletResponse portletResponse) {
        return _taskRecord.getDescription();
    }

    @Override
    public String getTitle(Locale locale) {
        return _taskRecord.getWorkPackage();
    }

    public String getType() {
        return TaskRecordAssetRendererFactory.TYPE;
    }

//    @Override
//    public PortletURL getURLEdit(LiferayPortletRequest liferayPortletRequest,
//            LiferayPortletResponse liferayPortletResponse) throws Exception {
//
//        PortletURL portletURL = locateTaskRecordManager(liferayPortletRequest);
//
//        portletURL.setParameter("tabs1", "settings");
//
//        return portletURL;
//    }

    @Override
    public String getURLView(LiferayPortletResponse liferayPortletResponse, WindowState windowState) {

        try {

            long portletPlid = PortalUtil.getPlidFromPortletId(_taskRecord.getGroupId(), false,
                    PortletKeys.TIMETRACKER);

            PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(portletPlid, PortletKeys.TIMETRACKER,
                    PortletRequest.RENDER_PHASE);

            portletURL.setParameter("mvcPath", "/edit_task_record.jsp");

            portletURL.setParameter("taskRecordId", String.valueOf(_taskRecord.getTaskRecordId()));

            return portletURL.toString();

        } catch (Exception e) {
            _log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public String getURLViewInContext(LiferayPortletRequest liferayPortletRequest,
            LiferayPortletResponse liferayPortletResponse, String noSuchEntryRedirect) {

        try {

            PortletURL portletURL = locateTaskRecordManager(liferayPortletRequest);

            return portletURL.toString();

        } catch (Exception e) {
            _log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public long getUserId() {
        return _taskRecord.getUserId();
    }

    @Override
    public String getUserName() {
        return _taskRecord.getUserName();
    }

    @Override
    public String getUuid() {
        return _taskRecord.getUuid();
    }

    @Override
    public boolean hasViewPermission(PermissionChecker permissionChecker) {

        return TaskRecordPermission.contains(permissionChecker, _taskRecord, ActionKeys.VIEW);
    }

    @Override
    public boolean include(HttpServletRequest request, HttpServletResponse response, String template) throws Exception {

        request.setAttribute(TimetrackerWebKeys.TASK_RECORD, _taskRecord);

        return super.include(request, response, template);
    }

    private PortletURL locateTaskRecordManager(LiferayPortletRequest liferayPortletRequest) throws PortalException {

        long portletPlid = PortalUtil.getPlidFromPortletId(_taskRecord.getGroupId(), false, PortletKeys.TIMETRACKER);

        PortletURL portletURL = PortletURLFactoryUtil.create(liferayPortletRequest, PortletKeys.TIMETRACKER,
                portletPlid, PortletRequest.RENDER_PHASE);

        portletURL.setParameter("mvcPath", "/edit_task_record.jsp");

        portletURL.setParameter("taskRecordId", String.valueOf(_taskRecord.getTaskRecordId()));

        return portletURL;
    }

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordAssetRenderer.class);

    private final TaskRecord _taskRecord;

}
