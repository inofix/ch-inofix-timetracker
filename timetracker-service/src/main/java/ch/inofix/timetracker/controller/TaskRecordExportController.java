package ch.inofix.timetracker.controller;

import static ch.inofix.timetracker.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_TASK_RECORDS_EXPORT_FAILED;
import static ch.inofix.timetracker.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_TASK_RECORDS_EXPORT_STARTED;
import static ch.inofix.timetracker.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_TASK_RECORDS_EXPORT_SUCCEEDED;
import static ch.inofix.timetracker.internal.exportimport.util.ExportImportLifecycleConstants.PROCESS_FLAG_TASK_RECORDS_EXPORT_IN_PROCESS;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.StopWatch;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.controller.ExportController;
import com.liferay.exportimport.kernel.controller.ExportImportController;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManager;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.xstream.XStreamAlias;
import com.liferay.exportimport.kernel.xstream.XStreamConverter;
import com.liferay.exportimport.kernel.xstream.XStreamType;
import com.liferay.exportimport.xstream.ConverterAdapter;
import com.liferay.exportimport.xstream.XStreamStagedModelTypeHierarchyPermission;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringPool;
//import com.liferay.portal.kernel.xml.Document;
//import com.liferay.portal.kernel.xml.Element;
//import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.xstream.configurator.XStreamConfigurator;
import com.liferay.xstream.configurator.XStreamConfiguratorRegistryUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import ch.inofix.timetracker.internal.exportimport.util.ExportImportThreadLocal;
import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordLocalService;

/**
 * @author Christian Berndt
 * @created 2017-04-21 19:23
 * @modified 2017-05-30 18:40
 * @version 1.0.1
 */
@Component(immediate = true, property = { "model.class.name=ch.inofix.timetracker.model.TaskRecord" }, service = {
        ExportImportController.class, TaskRecordExportController.class })
public class TaskRecordExportController implements ExportController {

    public TaskRecordExportController() {
        initXStream();
    }

    @Override
    public File export(ExportImportConfiguration exportImportConfiguration) throws Exception {

        PortletDataContext portletDataContext = null;

        try {

            ExportImportThreadLocal.setTaskRecordExportInProcess(true);

            portletDataContext = getPortletDataContext(exportImportConfiguration);

            exportImportConfiguration.getSettingsMap();

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_TASK_RECORDS_EXPORT_STARTED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext));

            File file = doExport(portletDataContext);

            ExportImportThreadLocal.setTaskRecordExportInProcess(false);

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_TASK_RECORDS_EXPORT_SUCCEEDED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext));

            return file;

        } catch (Throwable t) {

            _log.error(t);

            ExportImportThreadLocal.setTaskRecordExportInProcess(false);

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_TASK_RECORDS_EXPORT_FAILED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext), t);

            throw t;
        }
    }

    protected File doExport(PortletDataContext portletDataContext) throws Exception {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        StringBuilder sb = new StringBuilder();
        sb.append("<TaskRecords>");
        sb.append(StringPool.NEW_LINE);

        ActionableDynamicQuery actionableDynamicQuery = _taskRecordLocalService.getActionableDynamicQuery();

        // TODO: process date-range of portletDataContext

        actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<TaskRecord>() {

            @Override
            public void performAction(TaskRecord taskRecord) {
                String xml = _xStream.toXML(taskRecord);
                sb.append(xml);
                sb.append(StringPool.NEW_LINE);
            }

        });

        actionableDynamicQuery.performActions();

        sb.append("</TaskRecords>");

        if (_log.isInfoEnabled()) {
            _log.info("Exporting taskRecords takes " + stopWatch.getTime() + " ms");
        }

        portletDataContext.addZipEntry("/TaskRecords.xml", sb.toString());

        ZipWriter zipWriter = portletDataContext.getZipWriter();

        return zipWriter.getFile();

    }

    protected PortletDataContext getPortletDataContext(ExportImportConfiguration exportImportConfiguration)
            throws PortalException {

        Map<String, Serializable> settingsMap = exportImportConfiguration.getSettingsMap();

        String fileName = MapUtil.getString(settingsMap, "fileName");

        long sourcePlid = MapUtil.getLong(settingsMap, "sourcePlid");
        long sourceGroupId = MapUtil.getLong(settingsMap, "sourceGroupId");
        String portletId = MapUtil.getString(settingsMap, "portletId");
        Map<String, String[]> parameterMap = (Map<String, String[]>) settingsMap.get("parameterMap");
        DateRange dateRange = ExportImportDateUtil.getDateRange(exportImportConfiguration);

        Layout layout = _layoutLocalService.getLayout(sourcePlid);
        ZipWriter zipWriter = ExportImportHelperUtil.getPortletZipWriter(portletId);

        PortletDataContext portletDataContext = PortletDataContextFactoryUtil.createExportPortletDataContext(
                layout.getCompanyId(), sourceGroupId, parameterMap, dateRange.getStartDate(), dateRange.getEndDate(),
                zipWriter);

        portletDataContext.setOldPlid(sourcePlid);
        portletDataContext.setPlid(sourcePlid);
        portletDataContext.setPortletId(portletId);

        return portletDataContext;
    }

    protected int getProcessFlag() {
        return PROCESS_FLAG_TASK_RECORDS_EXPORT_IN_PROCESS;
    }

    /**
     * From com.liferay.exportimport.lar.PortletDataContextImpl
     */
    protected void initXStream() {

        _xStream = new XStream(null, new XppDriver(), new ClassLoaderReference(
                XStreamConfiguratorRegistryUtil.getConfiguratorsClassLoader(XStream.class.getClassLoader())));

        _xStream.omitField(HashMap.class, "cache_bitmask");

        Set<XStreamConfigurator> xStreamConfigurators = XStreamConfiguratorRegistryUtil.getXStreamConfigurators();

        if (SetUtil.isEmpty(xStreamConfigurators)) {
            return;
        }

        List<String> allowedTypeNames = new ArrayList<>();

        for (XStreamConfigurator xStreamConfigurator : xStreamConfigurators) {
            List<XStreamAlias> xStreamAliases = xStreamConfigurator.getXStreamAliases();

            if (ListUtil.isNotEmpty(xStreamAliases)) {
                for (XStreamAlias xStreamAlias : xStreamAliases) {
                    _xStream.alias(xStreamAlias.getName(), xStreamAlias.getClazz());
                }
            }

            List<XStreamConverter> xStreamConverters = xStreamConfigurator.getXStreamConverters();

            if (ListUtil.isNotEmpty(xStreamConverters)) {
                for (XStreamConverter xStreamConverter : xStreamConverters) {
                    _xStream.registerConverter(new ConverterAdapter(xStreamConverter), XStream.PRIORITY_VERY_HIGH);
                }
            }

            List<XStreamType> xStreamTypes = xStreamConfigurator.getAllowedXStreamTypes();

            if (ListUtil.isNotEmpty(xStreamTypes)) {
                for (XStreamType xStreamType : xStreamTypes) {
                    allowedTypeNames.add(xStreamType.getTypeExpression());
                }
            }
        }

        // For default permissions, first wipe than add default

        _xStream.addPermission(NoTypePermission.NONE);

        // Add permissions

        _xStream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        _xStream.addPermission(XStreamStagedModelTypeHierarchyPermission.STAGED_MODELS);

        _xStream.allowTypes(_XSTREAM_DEFAULT_ALLOWED_TYPES);

        _xStream.allowTypeHierarchy(List.class);
        _xStream.allowTypeHierarchy(Map.class);
        _xStream.allowTypeHierarchy(Timestamp.class);
        _xStream.allowTypeHierarchy(Set.class);

        _xStream.allowTypes(allowedTypeNames.toArray(new String[0]));

        _xStream.allowTypesByWildcard(new String[] { "com.thoughtworks.xstream.mapper.DynamicProxyMapper*" });
    }

    @Reference(unbind = "-")
    protected void setExportImportLifecycleManager(ExportImportLifecycleManager exportImportLifecycleManager) {
        _exportImportLifecycleManager = exportImportLifecycleManager;
    }

    @Reference(unbind = "-")
    protected void setLayoutLocalService(LayoutLocalService layoutLocalService) {
        _layoutLocalService = layoutLocalService;
    }

    @Reference(unbind = "-")
    protected void setTaskRecordLocalService(TaskRecordLocalService taskRecordLocalService) {

        _taskRecordLocalService = taskRecordLocalService;
    }

    private static final Class<?>[] _XSTREAM_DEFAULT_ALLOWED_TYPES = { boolean[].class, byte[].class, Date.class,
            Date[].class, double[].class, float[].class, int[].class, Locale.class, long[].class, Number.class,
            Number[].class, short[].class, String.class, String[].class };

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordExportController.class);

    private ExportImportLifecycleManager _exportImportLifecycleManager;
    private LayoutLocalService _layoutLocalService;
    private TaskRecordLocalService _taskRecordLocalService;
    private transient XStream _xStream;

}