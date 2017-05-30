package ch.inofix.timetracker.web.configuration;

import com.liferay.portal.kernel.util.StringPool;

/**
 *
 * @author Christian Berndt
 * @created 2017-05-16 20:30
 * @modified 2017-05-16 20:30
 * @version 1.0.0
 *
 */
public class ExportImportConfigurationConstants {
    public static final int TYPE_EXPORT_TASK_RECORDS = 0;

    public static final String TYPE_EXPORT_TASK_RECORDS_LABEL = "export-task-records";

    public static final int TYPE_IMPORT_TASK_RECORDS = 1;

    public static final String TYPE_IMPORT_TASK_RECORDS_LABEL = "import-task-records";

    public static String getTypeLabel(int type) {
        if (type == TYPE_EXPORT_TASK_RECORDS) {
            return TYPE_EXPORT_TASK_RECORDS_LABEL;
        } else if (type == TYPE_IMPORT_TASK_RECORDS) {
            return TYPE_IMPORT_TASK_RECORDS_LABEL;
        } else {
            return StringPool.BLANK;
        }
    }

}
