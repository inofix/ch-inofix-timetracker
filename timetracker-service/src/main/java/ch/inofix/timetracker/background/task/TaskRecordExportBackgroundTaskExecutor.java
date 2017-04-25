package ch.inofix.timetracker.background.task;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import ch.inofix.timetracker.service.TaskRecordLocalServiceUtil;

public class TaskRecordExportBackgroundTaskExecutor extends BaseExportImportBackgroundTaskExecutor {

    public TaskRecordExportBackgroundTaskExecutor() {
        // TODO
        // setBackgroundTaskStatusMessageTranslator(new
        // TaskRecordExportImportBackgroundTaskStatusMessageTranslator());
    }

    @Override
    public BackgroundTaskExecutor clone() {
        TaskRecordExportBackgroundTaskExecutor taskRecordExportBackgroundTaskExecutor = new TaskRecordExportBackgroundTaskExecutor();

        taskRecordExportBackgroundTaskExecutor
                .setBackgroundTaskStatusMessageTranslator(getBackgroundTaskStatusMessageTranslator());
        taskRecordExportBackgroundTaskExecutor.setIsolationLevel(getIsolationLevel());

        return taskRecordExportBackgroundTaskExecutor;
    }

    @Override
    public BackgroundTaskResult execute(BackgroundTask backgroundTask) throws PortalException {

        _log.info("execute");

        ExportImportConfiguration exportImportConfiguration = getExportImportConfiguration(backgroundTask);

        _log.info("groupId = " + exportImportConfiguration.getGroupId());

        Map<String, Serializable> settingsMap = exportImportConfiguration.getSettingsMap();

        long userId = MapUtil.getLong(settingsMap, "userId");

        StringBundler sb = new StringBundler(4);

        sb.append(StringUtil.replace(exportImportConfiguration.getName(), CharPool.SPACE, CharPool.UNDERLINE));
        sb.append(StringPool.DASH);
        sb.append(Time.getTimestamp());
        sb.append(".xml");

        _log.info(sb.toString());

        File xmlFile = TaskRecordLocalServiceUtil.exportTaskRecordsAsFile(exportImportConfiguration);

        // BackgroundTaskManagerUtil.addBackgroundTaskAttachment(userId,
        // backgroundTask.getBackgroundTaskId(),
        // sb.toString(), larFile);

        return BackgroundTaskResult.SUCCESS;
    }

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordExportBackgroundTaskExecutor.class.getName());

}
