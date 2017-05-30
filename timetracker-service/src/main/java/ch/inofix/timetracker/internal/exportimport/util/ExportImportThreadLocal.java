package ch.inofix.timetracker.internal.exportimport.util;

import com.liferay.portal.kernel.util.AutoResetThreadLocal;

/**
 *
 * @author Christian Berndt
 * @created 2017-05-24 21:31
 * @modified 2017-05-24 21:31
 * @version 1.0.0
 *
 *          Based on the model of
 *          com.liferay.exportimport.kernel.lar.ExportImportThreadLocal
 *
 */
public class ExportImportThreadLocal {

    public static boolean isDataDeletionImportInProcess() {
        if (isTaskRecordDataDeletionImportInProcess() || isPortletDataDeletionImportInProcess()) {

            return true;
        }

        return false;
    }

    public static boolean isExportInProcess() {
        if (isTaskRecordExportInProcess() || isPortletExportInProcess()) {
            return true;
        }

        return false;
    }

    public static boolean isImportInProcess() {
        if (isDataDeletionImportInProcess() || isTaskRecordImportInProcess() || isTaskRecordValidationInProcess()
                || isPortletImportInProcess() || isPortletValidationInProcess()) {

            return true;
        }

        return false;
    }

    public static boolean isInitialTaskRecordStagingInProcess() {
        return _initialTaskRecordStagingInProcess.get();
    }

    public static boolean isTaskRecordDataDeletionImportInProcess() {
        return _taskRecordDataDeletionImportInProcess.get();
    }

    public static boolean isTaskRecordExportInProcess() {
        return _taskRecordExportInProcess.get();
    }

    public static boolean isTaskRecordImportInProcess() {
        return _taskRecordImportInProcess.get();
    }

    public static boolean isTaskRecordStagingInProcess() {
        return _taskRecordStagingInProcess.get();
    }

    public static boolean isTaskRecordValidationInProcess() {
        return _taskRecordValidationInProcess.get();
    }

    public static boolean isPortletDataDeletionImportInProcess() {
        return _portletDataDeletionImportInProcess.get();
    }

    public static boolean isPortletExportInProcess() {
        return _portletExportInProcess.get();
    }

    public static boolean isPortletImportInProcess() {
        return _portletImportInProcess.get();
    }

    public static boolean isPortletStagingInProcess() {
        return _portletStagingInProcess.get();
    }

    public static boolean isPortletValidationInProcess() {
        return _portletValidationInProcess.get();
    }

    public static boolean isStagingInProcess() {
        if (isTaskRecordStagingInProcess() || isPortletStagingInProcess()) {
            return true;
        }

        return false;
    }

    public static void setInitialTaskRecordStagingInProcess(boolean initialTaskRecordStagingInProcess) {

        _initialTaskRecordStagingInProcess.set(initialTaskRecordStagingInProcess);
    }

    public static void setTaskRecordDataDeletionImportInProcess(boolean taskRecordDataDeletionImportInProcess) {

        _taskRecordDataDeletionImportInProcess.set(taskRecordDataDeletionImportInProcess);
    }

    public static void setTaskRecordExportInProcess(boolean taskRecordExportInProcess) {
        _taskRecordExportInProcess.set(taskRecordExportInProcess);
    }

    public static void setTaskRecordImportInProcess(boolean taskRecordImportInProcess) {
        _taskRecordImportInProcess.set(taskRecordImportInProcess);
    }

    public static void setTaskRecordStagingInProcess(boolean taskRecordStagingInProcess) {

        _taskRecordStagingInProcess.set(taskRecordStagingInProcess);
    }

    public static void setTaskRecordValidationInProcess(boolean taskRecordValidationInProcess) {

        _taskRecordValidationInProcess.set(taskRecordValidationInProcess);
    }

    public static void setPortletDataDeletionImportInProcess(boolean portletDataDeletionImportInProcess) {

        _portletDataDeletionImportInProcess.set(portletDataDeletionImportInProcess);
    }

    public static void setPortletExportInProcess(boolean portletExportInProcess) {

        _portletExportInProcess.set(portletExportInProcess);
    }

    public static void setPortletImportInProcess(boolean portletImportInProcess) {

        _portletImportInProcess.set(portletImportInProcess);
    }

    public static void setPortletStagingInProcess(boolean portletStagingInProcess) {

        _portletStagingInProcess.set(portletStagingInProcess);
    }

    public static void setPortletValidationInProcess(boolean portletValidationInProcess) {

        _portletValidationInProcess.set(portletValidationInProcess);
    }

    private static final ThreadLocal<Boolean> _initialTaskRecordStagingInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._initialTaskRecordStagingInProcess", false);
    private static final ThreadLocal<Boolean> _taskRecordDataDeletionImportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._taskRecordDataDeletionImportInProcess", false);
    private static final ThreadLocal<Boolean> _taskRecordExportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._taskRecordExportInProcess", false);
    private static final ThreadLocal<Boolean> _taskRecordImportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._taskRecordImportInProcess", false);
    private static final ThreadLocal<Boolean> _taskRecordStagingInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._taskRecordStagingInProcess", false);
    private static final ThreadLocal<Boolean> _taskRecordValidationInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._taskRecordValidationInProcess", false);
    private static final ThreadLocal<Boolean> _portletDataDeletionImportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._portletDataDeletionImportInProcess", false);
    private static final ThreadLocal<Boolean> _portletExportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._portletExportInProcess", false);
    private static final ThreadLocal<Boolean> _portletImportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._portletImportInProcess", false);
    private static final ThreadLocal<Boolean> _portletStagingInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._portletStagingInProcess", false);
    private static final ThreadLocal<Boolean> _portletValidationInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._portletValidationInProcess", false);

}
