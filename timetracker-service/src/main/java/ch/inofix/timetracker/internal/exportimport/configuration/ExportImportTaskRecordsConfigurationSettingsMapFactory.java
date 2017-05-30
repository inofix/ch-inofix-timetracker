package ch.inofix.timetracker.internal.exportimport.configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 *
 * @author Christian Berndt
 * @created 2017-05-27 16:11
 * @modified 2017-05-27 16:11
 * @version 1.0.0
 *
 */
public class ExportImportTaskRecordsConfigurationSettingsMapFactory {

    public static Map<String, Serializable> buildExportTaskRecordsSettingsMap(long userId, long sourcePlid,
            long sourceGroupId, String portletId, Map<String, String[]> parameterMap, Locale locale, TimeZone timeZone,
            String fileName) {

        return buildSettingsMap(userId, sourceGroupId, sourcePlid, 0, 0, portletId, null, null, null, parameterMap,
                StringPool.BLANK, 0, StringPool.BLANK, null, null, locale, timeZone, fileName);
    }

    /**
     * From com.liferay.exportimport.kernel.configuration.
     * ExportImportConfigurationSettingsMapFactory.
     */
    protected static Map<String, Serializable> buildSettingsMap(long userId, long sourceGroupId, long sourcePlid,
            long targetGroupId, long targetPlid, String portletId, Boolean privateLayout,
            Map<Long, Boolean> layoutIdMap, long[] layoutIds, Map<String, String[]> parameterMap, String remoteAddress,
            int remotePort, String remotePathContext, Boolean secureConnection, Boolean remotePrivateLayout,
            Locale locale, TimeZone timeZone, String fileName) {

        Map<String, Serializable> settingsMap = new HashMap<>();

        if (Validator.isNotNull(fileName)) {
            settingsMap.put("fileName", fileName);
        }

        if (MapUtil.isNotEmpty(layoutIdMap)) {
            HashMap<Long, Boolean> serializableLayoutIdMap = new HashMap<>(layoutIdMap);

            settingsMap.put("layoutIdMap", serializableLayoutIdMap);
        }

        if (ArrayUtil.isNotEmpty(layoutIds)) {
            settingsMap.put("layoutIds", layoutIds);
        }

        if (locale != null) {
            settingsMap.put("locale", locale);
        }

        if (parameterMap != null) {
            HashMap<String, String[]> serializableParameterMap = new HashMap<>(parameterMap);

            if (layoutIds != null) {
                serializableParameterMap.remove("layoutIds");
            }

            settingsMap.put("parameterMap", serializableParameterMap);
        }

        if (Validator.isNotNull(portletId)) {
            settingsMap.put("portletId", portletId);
        }

        if (privateLayout != null) {
            settingsMap.put("privateLayout", privateLayout);
        }

        if (Validator.isNotNull(remoteAddress)) {
            settingsMap.put("remoteAddress", remoteAddress);
        }

        if (Validator.isNotNull(remotePathContext)) {
            settingsMap.put("remotePathContext", remotePathContext);
        }

        if (remotePort > 0) {
            settingsMap.put("remotePort", remotePort);
        }

        if (remotePrivateLayout != null) {
            settingsMap.put("remotePrivateLayout", remotePrivateLayout);
        }

        if (secureConnection != null) {
            settingsMap.put("secureConnection", secureConnection);
        }

        if (sourceGroupId > 0) {
            settingsMap.put("sourceGroupId", sourceGroupId);
        }

        if (sourcePlid > 0) {
            settingsMap.put("sourcePlid", sourcePlid);
        }

        if (targetGroupId > 0) {
            settingsMap.put("targetGroupId", targetGroupId);
        }

        if (targetPlid > 0) {
            settingsMap.put("targetPlid", targetPlid);
        }

        if (timeZone != null) {
            settingsMap.put("timezone", timeZone);
        }

        settingsMap.put("userId", userId);

        return settingsMap;
    }

    private static final Log _log = LogFactoryUtil.getLog(ExportImportTaskRecordsConfigurationSettingsMapFactory.class);

}
