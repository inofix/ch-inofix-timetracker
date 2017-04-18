package ch.inofix.timetracker.internal.exportimport.data.handler;

import java.util.List;

import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.xml.Element;

import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalService;
import ch.inofix.timetracker.service.permission.TimetrackerPortletPermission;

/**
 *
 * @author Christian Berndt
 * @created 2017-04-17 16:42
 * @modified 2017-04-18 16:23
 * @version 1.0.1
 *
 */
@Component(immediate = true, property = {
        "javax.portlet.name=" + PortletKeys.TIMETRACKER }, service = PortletDataHandler.class)
public class TimetrackerPortletDataHandler extends BasePortletDataHandler {

    public static final String NAMESPACE = "timetracker";

    public static final String SCHEMA_VERSION = "1.0.0";

    @Override
    public String getSchemaVersion() {
        return SCHEMA_VERSION;
    }

    @Activate
    protected void activate() {

        setDeletionSystemEventStagedModelTypes(new StagedModelType(TaskRecord.class));

        setExportControls(new PortletDataHandlerBoolean(NAMESPACE, "task-records", true, false, null,
                TaskRecord.class.getName()));

        setImportControls(getExportControls());
    }

    @Override
    protected PortletPreferences doDeleteData(PortletDataContext portletDataContext, String portletId,
            PortletPreferences portletPreferences) throws Exception {

        if (portletDataContext.addPrimaryKey(TimetrackerPortletDataHandler.class, "deleteData")) {

            return portletPreferences;
        }

        _taskRecordLocalService.deleteGroupTaskRecords(portletDataContext.getScopeGroupId());

        return portletPreferences;
    }

    @Override
    protected String doExportData(final PortletDataContext portletDataContext, String portletId,
            PortletPreferences portletPreferences) throws Exception {

        Element rootElement = addExportDataRootElement(portletDataContext);

        if (!portletDataContext.getBooleanParameter(NAMESPACE, "task-records")) {
            return getExportDataRootElementString(rootElement);
        }

        portletDataContext.addPortletPermissions(TimetrackerPortletPermission.RESOURCE_NAME);

        rootElement.addAttribute("group-id", String.valueOf(portletDataContext.getScopeGroupId()));

        ActionableDynamicQuery actionableDynamicQuery = _taskRecordLocalService
                .getExportActionableDynamicQuery(portletDataContext);

        actionableDynamicQuery.performActions();

        return getExportDataRootElementString(rootElement);
    }

    @Override
    protected PortletPreferences doImportData(PortletDataContext portletDataContext, String portletId,
            PortletPreferences portletPreferences, String data) throws Exception {

        if (!portletDataContext.getBooleanParameter(NAMESPACE, "task-records")) {
            return null;
        }

        portletDataContext.importPortletPermissions(TimetrackerPortletPermission.RESOURCE_NAME);

        Element entriesElement = portletDataContext.getImportDataGroupElement(TaskRecord.class);

        List<Element> entryElements = entriesElement.elements();

        for (Element entryElement : entryElements) {
            StagedModelDataHandlerUtil.importStagedModel(portletDataContext, entryElement);
        }

        return null;
    }

    @Override
    protected void doPrepareManifestSummary(PortletDataContext portletDataContext,
            PortletPreferences portletPreferences) throws Exception {

        ActionableDynamicQuery actionableDynamicQuery = _taskRecordLocalService
                .getExportActionableDynamicQuery(portletDataContext);

        actionableDynamicQuery.performCount();
    }

    @Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
    protected void setModuleServiceLifecycle(ModuleServiceLifecycle moduleServiceLifecycle) {
    }

    @Reference(unbind = "-")
    protected void setTaskRecordLocalService(TaskRecordLocalService taskRecordLocalService) {

        _taskRecordLocalService = taskRecordLocalService;
    }

    private TaskRecordLocalService _taskRecordLocalService;

    private static Log _log = LogFactoryUtil.getLog(TimetrackerPortletDataHandler.class.getName());
}
