package ch.inofix.timetracker.background.task;

import java.io.File;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import ch.inofix.timetracker.service.TaskRecordLocalServiceUtil;

/**
 * @author Christian Berndt
 * @created 2017-05-30 20:07
 * @modified 2017-05-30 20:07
 * @version 1.0.0
 */
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

        ExportImportConfiguration exportImportConfiguration = getExportImportConfiguration(backgroundTask);

        long userId = backgroundTask.getUserId();

        StringBundler sb = new StringBundler(4);

        sb.append(StringUtil.replace(exportImportConfiguration.getName(), CharPool.SPACE, CharPool.UNDERLINE));
        sb.append(StringPool.DASH);
        sb.append(Time.getTimestamp());
        sb.append(".zip");

        File xmlFile = TaskRecordLocalServiceUtil.exportTaskRecordsAsFile(exportImportConfiguration);

        BackgroundTaskManagerUtil.addBackgroundTaskAttachment(userId, backgroundTask.getBackgroundTaskId(),
                sb.toString(), xmlFile);

        return BackgroundTaskResult.SUCCESS;
    }
}
