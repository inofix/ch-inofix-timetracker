package ch.inofix.timetracker.background.task;

import java.io.Serializable;
import java.util.Map;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.util.MapUtil;

import ch.inofix.timetracker.background.task.display.ExportImportBackgroundTaskDisplay;

/**
 *
 * @author Christian Berndt
 * @created 2017-04-05 00:09
 * @modified 2017-04-05 00:09
 * @version 1.0.0
 *
 */
public abstract class BaseExportImportBackgroundTaskExecutor extends BaseBackgroundTaskExecutor {

    public BaseExportImportBackgroundTaskExecutor() {
        // TODO
//        setBackgroundTaskStatusMessageTranslator(new DefaultExportImportBackgroundTaskStatusMessageTranslator());
    }

    @Override
    public BackgroundTaskDisplay getBackgroundTaskDisplay(BackgroundTask backgroundTask) {

        return new ExportImportBackgroundTaskDisplay(backgroundTask);
    }

    @Override
    public String handleException(BackgroundTask backgroundTask, Exception e) {
        JSONObject jsonObject = StagingUtil.getExceptionMessagesJSONObject(getLocale(backgroundTask), e,
                getExportImportConfiguration(backgroundTask));

        return jsonObject.toString();
    }

    protected ExportImportConfiguration getExportImportConfiguration(BackgroundTask backgroundTask) {

        Map<String, Serializable> taskContextMap = backgroundTask.getTaskContextMap();

        long exportImportConfigurationId = MapUtil.getLong(taskContextMap, "exportImportConfigurationId");

        return ExportImportConfigurationLocalServiceUtil.fetchExportImportConfiguration(exportImportConfigurationId);
    }

    protected static final TransactionConfig transactionConfig = TransactionConfig.Factory.create(Propagation.REQUIRED,
            new Class<?>[] { Exception.class });

}