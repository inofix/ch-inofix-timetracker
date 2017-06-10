package ch.inofix.timetracker.service.util;

import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import ch.inofix.timetracker.model.TaskRecord;
import ch.inofix.timetracker.service.TaskRecordServiceUtil;

/**
 *
 * @author Christian Berndt
 * @created 2017-06-10 18:38
 * @modified 2017-06-10 18:38
 * @version 1.0.0
 *
 */
public class TaskRecordUtil {

    public static List<TaskRecord> getTaskRecords(Hits hits) {

        List<Document> documents = ListUtil.toList(hits.getDocs());

        List<TaskRecord> taskRecords = new ArrayList<TaskRecord>();

        for (Document document : documents) {
            try {
                long taskRecordId = GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK));

                TaskRecord taskRecord = TaskRecordServiceUtil.getTaskRecord(taskRecordId);
                taskRecords.add(taskRecord);
            } catch (Exception e) {

                if (_log.isErrorEnabled()) {
                    _log.error(e.getMessage());
                }
            }
        }

        return taskRecords;
    }

    private static final Log _log = LogFactoryUtil.getLog(TaskRecordUtil.class.getName());

}
